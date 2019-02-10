import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/*
Copyright (C) 2019  S Combes

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. */

public class Sigaba extends ClassicalCipher {

static String gache="";

// Sigaba cipher.  Encodes A-Z and space; however any Z is first mapped to an X
// and hence the decode shows X for both original X and Z.  This is to allow spaces
// to also be encoded, using Z.

// As with MTC3, Index rotors cannot be reversed, although this might be allowable
// in the real system.

// Note MTC3 implementation is FLAWED.  It appears that, within each Control or
// Cipher group of offsets (i.e. the 5 letters e.g. AFGDE), if any wheel of the 5
// is reversed, then all non reversed wheels default to an offset of A, and all
// reversed wheels other than the first default to an offset of A.  The first
// reversed wheel uses the correct offset.
// e.g.  if control wheel reversal pattern is 01100 and
//                       intended offsets are XYZPQ
// then this processes as if the offsets were AYAAA

// Above was tested with 80,000 random encrypts using C code.  In each case, identical
// encryption with random offsets, and those random mapped as above.

// This code tested with 1M random encrypt/decrypt pairs that matched
// Cross-checked with c-code : 100,000 random encrypts match

// When static variable "mimicMTC3" is true, this behaviour is copied :
// However further processing may be required to avoid wasting testing equivalent cases

// There is a second error in the old MTC3 code.  Rotors advanced at the wrong point, always 
// at a fixed point after encryption started, rather than when O appeared in the window
// (or A when in reverse).  Likewise mimicMTC3 copies this behaviour.

// Rotors 0-9, used for both Control and Cipher Rotors.
// Data from MTC3 - not true original wheels 
String [] rotors={"YCHLQSUGBDIXNZKERPVJTAWFOM","INPXBWETGUYSAOCHVLDMQKZJFR",  // 0,1
                 "WNDRIOZPTAXHFJYQBMSVEKUCGL","TZGHOBKRVUXLQDMPNFWCJYEIAS",   // 2,3
                 "YWTAHRQJVLCEXUNGBIPZMSDFOK","QSLRBTEKOGAICFWYVMHJNXZUDP",   // 4,5
                 "CHJDQIGNBSAKVTUOXFWLEPRMZY","CDFAJXTIMNBEQHSUGRYLWZKVPO",   // 6,7
                 "XHFESZDNRBCGKQIJLTVMUOYAPW","EZJQXMOGYTCSFRIUPVNADLHWBK"};  // 8,9

// The top two form the stepping maze, all together are the rotor cage.
ControlRotor controlRotor;   // Set of 5
IndexRotor   indexRotor;     // ditto
CipherRotor  cipherRotor;    // ditto
int stream;                  // Positon in stream

boolean mimicMTC3=false;
static boolean FAST=false;  // Remove input checks, but also destroys toString()

protected Sigaba() { }  // Dummy for mainly static children

Sigaba(boolean mimicMTC3,
       int [] cipherOrder, int [] controlOrder, int [] indexOrder,
       int [] cipherOffset,int [] controlOffset,int [] indexOffset,
       boolean [] cipherRev, boolean [] controlRev) {

super(new Codespace("ABCDEFGHIJKLMNOPQRSTUVWXYZ ",
                    "ABCDEFGHIJKLMNOPQRSTUVWXYXZ",  // Z->X and space->Z ...
                    "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
                    "ABCDEFGHIJKLMNOPQRSTUVWXY ")); // ... and Z-> space

this.mimicMTC3=mimicMTC3;

if (!FAST) {
  boolean [] used=new boolean[10];
  for (int i=0;i<5;i++) {

    if (used[cipherOrder[i]])
      throw new IllegalArgumentException(
           "Sigaba : can't repeat rotors within control and cipher banks");
    used[cipherOrder[i]]=true;

    if (used[controlOrder[i]])
      throw new IllegalArgumentException(
           "Sigaba : can't repeat rotors within control and cipher banks");
    used[controlOrder[i]]=true;
  }
}
controlRotor=new ControlRotor(controlOrder,controlOffset,controlRev);
cipherRotor =new CipherRotor(cipherOrder,cipherOffset,cipherRev);
indexRotor  =new IndexRotor(indexOrder,indexOffset);
stream=0;
}
// ----------------------------------------------------------
@Override
public String encode(String PT) 
{ 
String flat=cs.flattenToPT(PT);
StringBuilder sb=new StringBuilder(flat.length());

for (int i=0;i<flat.length();i++) 
  sb.append((char)(65+cipherRotor.encrypt((int)flat.charAt(i)-65)));

return sb.toString();
}
// ----------------------------------------------------------
@Override
public String decode(String CT) 
{ 
StringBuilder sb=new StringBuilder(CT.length());

for (int i=0;i<CT.length();i++) {
  int c=cipherRotor.decrypt((int)CT.charAt(i)-65);  
  sb.append(cs.codeSpace.charAt(c));
}
return sb.toString();
}
// ----------------------------------------------------------------------------------
public void reset() {  // Zeroise the encryption position (not the cipher key)
  cipherRotor.position=new int[5]; 
  controlRotor.offset1=0;
  controlRotor.offset2=0;
  controlRotor.offset3=0;
  stream=0;
}
// ----------------------------------------------------------------------------------
public class CipherRotor {

int [][] rotor;   // The actual 5 rotors being used, with their initial offset
int [][] rotor2;  // The actual 5 rotors being used, without offset, as absolutes
int [][] decry;   // The equivalent for decryption
int [] myoffset;  // Start point offset
int [] position;  // The further, current offset (initialises as zero)
boolean [] rev;   // Revolution direction
byte [] lut;      // Lookup table
String key;
boolean [] present;

CipherRotor(int [] order,int [] offset,boolean [] rev) {

// N.B. need not copy order - enshrined in conversion rotors->rotor
myoffset=new int[5];    // Defensive copy to cope with MTC3 error

System.arraycopy(offset,0,myoffset,0,5);
if (mimicMTC3) {
  if (rev[0] || rev[1] || rev[2] || rev[3] || rev[4]) {  // Any reversal
    boolean found=false;
    for (int i=0;i<5;i++) {
      if (!rev[i]) myoffset[i]=0;  // Non-reversed have nulled offsets
      else {
        if (found) myoffset[i]=0;  // All but the first reversed has nulled offset
        found=true;
      }
    }
  }
}

if (!FAST) {
  boolean [] used=new boolean[10];
  for (int i=0;i<5;i++) {
    if (order[i]<0 || order[i]>9) 
      throw new IllegalArgumentException(
           "Cipher Rotors need to be identified by digits from 0-9");

    if (used[order[i]])
      throw new IllegalArgumentException(
           "Cipher Rotors need to be unique");
    used[order[i]]=true;

    if (offset[i]<0 || offset[i]>25) 
      throw new IllegalArgumentException(
           "Cipher Rotors need to have offsets with digits from 0-25");
  }
  StringBuilder sb=new StringBuilder();

  for (int q=0;q<5;q++) sb.append((char)(48+order[q]));
  for (int q=0;q<5;q++) sb.append(rev[q]?"1":"0");
  for (int q=0;q<5;q++) sb.append((char)(65+offset[q]));

  key=sb.toString();
}
else key="$$$$$$$$$$$$$$$$";

rotor=new int[5][26];
decry=new int[5][26];

rotor2=new int[5][26];

this.rev=new boolean[5];
for (int i=0;i<5;i++) {
  this.rev[i]=rev[i];
  for (int j=0;j<26;j++) {
    rotor2[i][j]=((int)rotors[order[i]].charAt(j)-65);
    rotor[i][j]=((int)rotors[order[i]].charAt((j+26+myoffset[i])%26)-65+52-j-myoffset[i])%26;
    int target =((int)rotors[order[i]].charAt((j+26+myoffset[i])%26)-65+26-myoffset[i])%26;
    decry[i][target]=(26+j-target)%26;
  }
  if (rev[i]) { // This rotor is reversed 
    int [] dummy=new int[26]; // A 'reversed' start-point wheel
    for (int j=0;j<26;j++) {
      dummy[(j+rotor[i][j])%26]=j;
    }
    for (int j=0;j<26;j++) {
      rotor[i][j]=(dummy[j]+26-j)%26;
      int target =dummy[j]%26;
      decry[i][target]=(26+j-target)%26;
    }
  }
}
position=new int[5]; // Rely on init to zero
}
//--------------------------------------------------------------------------------------------
public void doAdvance(int advance) {

for (int i=0;i<5;i++) {   
  if ((advance&(1<<i))!=0) {
    if (rev[i]) position[i]+=25;
    else        position[i]++;
    if (position[i]>25)
      position[i]-=26;
  }
}
}
//--------------------------------------------------------------------------------------------
public int encrypt(int input) {

// One encryption of a single character by the current cipher rotors.
// Increments the control rotors and then the cipher rotors (after the enryption)

// Cosmetic : report rotor letters showing

StringBuffer sb=new StringBuffer(20);

for (int i=0;i<5;i++) 
  sb.append((char)(65+(26+myoffset[i]-position[i])%26));

sb.append(" ");

sb.append((char)(65+(controlRotor.myoffset[0])%26));  // Does not move
sb.append((char)(65+(controlRotor.myoffset[1]+controlRotor.offset1+26)%26));  
sb.append((char)(65+(controlRotor.myoffset[2]+controlRotor.offset2+26)%26));  
sb.append((char)(65+(controlRotor.myoffset[3]+controlRotor.offset3+26)%26));  
sb.append((char)(65+(controlRotor.myoffset[4])%26));  // Does not move

stream++;
// Passes L to R
int interim=input+rotor[0][(input+26-position[0])%26]; // Encrypt with 1st rotor
interim+=rotor[1][(interim+26-position[1])%26];                     // 2nd rotor
interim+=rotor[2][(interim+26-position[2])%26];                     // 3rd rotor
interim+=rotor[3][(interim+26-position[3])%26];                     // 4th rotor
interim+=rotor[4][(interim+26-position[4])%26];                     // 5th rotor

int shift=0;
int index=0;
for (int i=0;i<4;i++) {
  shift=26+(position[i]+myoffset[i]);
  index=(index<<5)|(shift%26);
}
shift=26+(position[4]+myoffset[4]);
index=(index<<5)|((input+shift)%26);

// Advance the Cipher rotors based on the Control and Index rotors
int advance=indexRotor.map(controlRotor.encrypt());

doAdvance(advance);

return interim%26;
}
//--------------------------------------------------------------------------------------------
public int decrypt(int input) { return decrypt(input,true); }
// One decryption of a single character by the current cipher rotors.
// Increments the control rotors and then the cipher rotors (after the enryption)
//--------------------------------------------------------------------------------------------
public int decrypt(int input,boolean adv) {

// One decryption of a single character by the current cipher rotors.
// Increments the control rotors and then the cipher rotors (after the 
// decryption) if adv is true

stream++;
// Passes R to L
int interim=input+decry[4][(26+input-position[4])%26]; // Decrypt with 5th rotor
interim+=decry[3][(interim+26-position[3])%26];                     // 4th rotor
interim+=decry[2][(interim+26-position[2])%26];                     // 3rd rotor
interim+=decry[1][(interim+26-position[1])%26];                     // 2nd rotor
interim+=decry[0][(interim+26-position[0])%26];                     // 1st rotor

if (adv) {
  // Advance the Cipher rotors based on the Control and Index rotors  
  int advance=indexRotor.map(controlRotor.encrypt());
  doAdvance(advance);
}
return interim%26;
}
}
// ----------------------------------------------------------------------------------
public class ControlRotor {

int [][] rotor;
int offset1;
int offset2;
int offset3;
int [] myoffset;
boolean rev1;
boolean rev2;
boolean rev3;
String key;

ControlRotor(int [] order,int [] offset,boolean [] rev) {

// N.B. need not copy order - enshrined in conversion rotors->rotor

offset1=0;
offset2=0;
offset3=0;

myoffset=new int[5];    // Defensive copy to cope with MTC3 error
// But also provides ability to report current rotor positions (presentational only)
System.arraycopy(offset,0,myoffset,0,5);

if (mimicMTC3) {
  if (rev[0] || rev[1] || rev[2] || rev[3] || rev[4]) {  // Any reversal
    boolean found=false;
    for (int i=0;i<5;i++) {
      if (!rev[i]) myoffset[i]=0;  // Non-reversed have nulled offsets
      else {
        if (found) myoffset[i]=0;  // All but the first reversed has nulled offset
        found=true;
      }
    }
  }
}
rev1=rev[1]; // For stepover
rev2=rev[2]; // For stepover
rev3=rev[3]; // For stepover

if (!FAST) {
  boolean [] used=new boolean[10];
  for (int i=0;i<5;i++) {
    if (order[i]<0 || order[i]>9) 
      throw new IllegalArgumentException(
         "Control Rotors need to be identified by digits from 0-9");

    if (used[order[i]])
      throw new IllegalArgumentException(
           "Control Rotors need to be unique");
    used[order[i]]=true;

    if (offset[i]<0 || offset[i]>25) 
      throw new IllegalArgumentException(
         "Control Rotors need to have offsets with digits from 0-25");
  }
  StringBuilder sb=new StringBuilder();

  for (int q=0;q<5;q++) sb.append((char)(48+order[q]));
  for (int q=0;q<5;q++) sb.append(rev[q]?"1":"0");
  for (int q=0;q<5;q++) sb.append((char)(65+offset[q]));

  key=sb.toString();
}
else key="$$$$$$$$$$$$$$$$";

rotor=new int[5][26];

for (int i=0;i<5;i++) {
  for (int j=0;j<26;j++) { // Traverse R-L so must invert rotor as follows.
    int target=((int)rotors[order[i]].charAt((j+26+myoffset[i])%26)-65+26-myoffset[i])%26;
    rotor[i][target]=(26+j-target)%26;
  }
  if (rev[i]) { // This rotor is reversed 
    int [] dummy=new int[26]; // A pseudo-reversed start-point
    for (int j=0;j<26;j++) {
      dummy[(j+rotor[i][j])%26]=j;
    }
    for (int j=0;j<26;j++) {
      rotor[i][j]=(dummy[j]+26-j)%26;
    }
  }
}
}
//--------------------------------------------------------------------------------------------
public int encrypt() {

final int [] mapping={9,1,2,3,3,4,4,4,5,5,5,6,6,6,6,7,7,7,7,7,8,8,8,8,8,8};
final int [] input={5,6,7,8}; 
// FGHI as assumed in Stamp, SIGABA: Cryptanalysis of the Full Keyspace

// One encryption step of a group of inputs to the stepping maze
// Return array of inputs to the index rotors, 0-9.

int result=0;

for (int i : input) {  // Passes R to L
  int interim=i+rotor[4][i];       // Encrypt with 5th rotor
  interim+=rotor[3][(interim+offset3)%26];      // 4th rotor
  interim+=rotor[2][(interim+offset2)%26];      // 3rd rotor
  interim+=rotor[1][(interim+offset1)%26];      // 2nd rotor
  interim+=rotor[0][interim%26];                // 1st rotor

  result|=(1<<(mapping[interim%26]-1));
}
if (mimicMTC3) {  // MTC3 steps at the 13th character and MOD(26) variants later
// while saying it steps at the letter 'O'.  This is true for an initial 'A' offset, but false
// otherwise.  Ironically effect largely masked by other MTC3 error - which sets most initial offsets to 'A'
  if (rev2)    offset2=(offset2+1)%26;
  else         offset2=(offset2+25)%26;

  if (offset2%26==13) {
    if (rev3)  offset3=(offset3+1)%26;    
    else       offset3=(offset3+25)%26;    
    if (offset3%26==13) {
      if (rev1)  offset1=(offset1+1)%26;  
      else       offset1=(offset1+25)%26;  
    }
  }
}
else {

  if ((!rev2 && ((myoffset[2]+offset2)%26==14)) ||  // At 'O' in FWD direction
      ( rev2 && ((myoffset[2]+offset2)%26==0))) {   // At 'A' in REV direction

    if ((!rev3 && ((myoffset[3]+offset3)%26==14)) ||  // At 'O' in FWD direction
        ( rev3 && ((myoffset[3]+offset3)%26==0))) {   // At 'A' in REV direction
      if (rev1)  offset1=(offset1+1)%26;  
      else       offset1=(offset1+25)%26;  
    }

    if (rev3)  offset3=(offset3+1)%26;    
    else       offset3=(offset3+25)%26;    
  }

  if (rev2)    offset2=(offset2+1)%26;  // Always step the middle
  else         offset2=(offset2+25)%26;
}
return result;
}
}
// ----------------------------------------------------------------------------------
public class IndexRotor {

int [][] rotor={{7,5,9,1,4,8,2,6,3,0},  // From MTC3 - and believed true originals
                {3,8,1,0,5,9,2,7,6,4},  // from at least one instantiation
                {4,0,8,6,1,5,3,2,9,7},  // see http://www.quadibloc.com/crypto/ro0205.htm
                {3,9,8,0,5,2,6,1,7,4},  // and http://www.maritime.org/tech/ecm2.htm
                {6,4,9,7,1,3,5,2,8,0}};

int [] index;    // the combined 10-permutation
String key;

IndexRotor(int [] order,int [] offset) {

boolean [] used=new boolean[5];
for (int i=0;i<5;i++) {
  if (order[i]<0 || order[i]>4) 
    throw new IllegalArgumentException(
         "Index Rotors need to be in order with digits from 0-4");

  if (used[order[i]])
    throw new IllegalArgumentException(
         "Index Rotors need to be in unique order from 0-4");
  used[order[i]]=true;

  if (offset[i]<0 || offset[i]>9) 
    throw new IllegalArgumentException(
         "Index Rotors need to have offsets with digits from 0-9");
}
StringBuilder sb=new StringBuilder();

for (int q=0;q<5;q++)  sb.append((char)(48+order[q]));
for (int q=0;q<5;q++)  sb.append((char)(48+offset[q]));

key=sb.toString();

index=new int[10];

for (int i=0;i<10;i++) {
  index[i]=i;  // Start input
  for (int r=0;r<5;r++) // Run through the 5 consecutive rotors
    index[i]=(rotor[order[r]][(10+index[i]+offset[r])%10]+10-offset[r])%10;
}
}
//-------------------------------------------------------------------------------
int map(int input) {

int result=0;
for (int j=1;j<10;j++) {  // Bit 0 is always 0, so ignore
  if ((input&(1<<(j-1)))!=0) { 
    int out=index[j];
    if (out==0 || out==9) result|=1;
    if (out==7 || out==8) result|=2;
    if (out==5 || out==6) result|=4;
    if (out==3 || out==4) result|=8;
    if (out==1 || out==2) result|=16;
  }
}
return result;
}
}
//-------------------------------------------------------------------------------
public String getKey() {

return    (cipherRotor.key.substring(0,5)+
           controlRotor.key.substring(0,5)+
           indexRotor.key.substring(0,5)+" "+
           cipherRotor.key.substring(5,10)+
           controlRotor.key.substring(5,10)+" "+
           cipherRotor.key.substring(10,15)+
           controlRotor.key.substring(10,15)+
           indexRotor.key.substring(5,10));
}
//-------------------------------------------------------------------------------
public String toString() { // Same format as MTC3 Sigaba.c

return (this.getClass().getName()+" Cipher : Key "+nL+getKey()+ 
               nL+super.toString()+nL);
}
//-------------------------------------------------------------------------------
public static Sigaba randomFactory(boolean MTC3) {  // Give me a random machine

String wheels=RandomShuffle.shuffle("0123456789");
String indexW=RandomShuffle.shuffle("01234");

String cio="";
String coo="";
String io="";
String cirev="";
String corev="";

int [] cipherOrder=new int[5];
int [] controlOrder=new int[5];
int [] indexOrder=new int[5];

int [] cipherOffset=new int[5];
int [] controlOffset=new int[5];
int [] indexOffset=new int[5];

boolean [] cipherRev=new boolean[5];
boolean [] controlRev=new boolean[5];


for (int i=0;i<5;i++) {
  cipherOrder[i]=(int)wheels.charAt(i)-48;
  controlOrder[i]=(int)wheels.charAt(i+5)-48;
  indexOrder[i]=(int)indexW.charAt(i)-48;
  cipherOffset[i]=rand.nextInt(26);
  controlOffset[i]=rand.nextInt(26);
  indexOffset[i]=rand.nextInt(10);
  cipherRev[i]=(rand.nextInt(2)==0);
  controlRev[i]=(rand.nextInt(2)==0);
  cio+=(char)(65+cipherOffset[i]);
  coo+=(char)(65+controlOffset[i]);
  io+=(char)(48+indexOffset[i]);
  cirev+=(cipherRev[i]?"1":"0");
  corev+=(controlRev[i]?"1":"0");
}

return new Sigaba(MTC3,cipherOrder, controlOrder, indexOrder,
                         cipherOffset,controlOffset,indexOffset,
                         cipherRev,   controlRev);


}
//-------------------------------------------------------------------------------
public static Sigaba deterministicFactory(boolean MTC3,String PT,boolean [] cipherRev,String init) {

// Use the 1st 4 characters of PT to set the control wheels
// deterministically - for testing purposes only

String indexW=RandomShuffle.shuffle("01234");

String cio="";
String coo="";
String io="";
String cirev="";
String corev="";

int [] cipherOrder=new int[5];
int [] controlOrder=new int[5];
int [] indexOrder=new int[5];

int [] cipherOffset=new int[5];
int [] controlOffset=new int[5];
int [] indexOffset=new int[5];

boolean [] controlRev=new boolean[5];

int deter=26*26*26*((int)PT.charAt(0)-65)+26*26*((int)PT.charAt(1)-65)+
   26*((int)PT.charAt(2)-65)+((int)PT.charAt(3)-65);

boolean [] wh=new boolean[10];

for (int i=10;i>5;i--) {

  int tmp=deter%i;
  while (wh[tmp]) tmp++; // tmp=(tmp+1)%10;
  wh[tmp]=true;
  deter/=i;
  cipherOrder[10-i]=tmp;
}

String rest="";
for (int i=0;i<10;i++)
  if (!wh[i]) rest+=(char)(48+i);

String wheels=RandomShuffle.shuffle(rest);

for (int i=0;i<5;i++) {
  controlOrder[i]=(int)wheels.charAt(i)-48;
  indexOrder[i]=(int)indexW.charAt(i)-48;
  cipherOffset[i]=(int)init.charAt(i)-65;
  controlOffset[i]=rand.nextInt(26);
  indexOffset[i]=rand.nextInt(10);
  controlRev[i]=(rand.nextInt(2)==0);
  cio+=(char)(65+cipherOffset[i]);
  coo+=(char)(65+controlOffset[i]);
  io+=(char)(48+indexOffset[i]);
  cirev+=(cipherRev[i]?"1":"0");
  corev+=(controlRev[i]?"1":"0");
}

return new Sigaba(MTC3,cipherOrder, controlOrder, indexOrder,
                         cipherOffset,controlOffset,indexOffset,
                         cipherRev,   controlRev);

}
//-------------------------------------------------------------------------------
public static void main(String [] args) {

// Test from MTC3 challenge additional files
String StampCT="SGYRGRHCQQXBBMBLDCFUJNEEEBPGNYCZYOWZYBORMREBQBGMJWQURFQQAOIKGNNOCSFRLWUADGLUMEQIDDEWAEEDCYJJINVXDJCHODWRAOJSINFGAPTRUUNYTQVBZTTABDWZNMAVEEOK";
String StampPT="AD HOC AD LOC QUID PRO QUO SO LITTLE TIME SO MUCH TO KNOW FOUR SCORE AND SEVEN YEARS AGO SPACE THE FINAL FRONTIER IN THE BEGINNING BUZZ BUZZ";

// Encryption is "Sigaba 987601234501243 1100001010 AAABBCCCDD98703 0 plain.txt cipher.txt"

int [] cipherOrder ={9,8,7,6,0};
boolean [] cipherRev={true,true,false,false,false};
int [] cipherOffset={0,0,0,1,1}; 

int [] controlOrder ={1,2,3,4,5};
boolean [] controlRev={false,true,false,true,false};
int [] controlOffset={2,2,2,3,3};

int [] indexOrder ={0,1,2,4,3};
int [] indexOffset={9,8,7,0,3}; 

Sigaba sigaba1=new Sigaba(true,cipherOrder, controlOrder, indexOrder,
                         cipherOffset,controlOffset,indexOffset,
                         cipherRev,   controlRev);

if (sigaba1.decode(StampCT).equals(StampPT.replace("Z","X"))) System.out.println("PASS");
else                                                          System.out.println("*** FAIL ***");
}
}