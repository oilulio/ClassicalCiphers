public class Tunny extends ClassicalCipher
{
// *** On Unix machines compile with an encoding such as ISO-8859-1
// i.e. "javac -encoding ISO-8859-1 Tunny.java"   *************
// This is to cope with the "£" sign in the text (and the umlaut in Schlüsselzusatz)
  
  
// Conducts Tunny encryption and decryption.
// i.e. a Lorentz Schlüsselzusatz SZ40
// see http://en.wikipedia.org/w/index.php?title=Cryptanalysis_of_the_Lorenz_cipher&oldid=579291094
// and Colossus by B Jack Copeland, ISBN 978-0-19-957814-6

// 'x' = 'pulse'  = binary 1
// '.' = no pulse = binary 0

// 'xxx..' is U (Copeland p48)

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

final static int ZERO=0;   // Zeroed wheels
final static int POETRY=1; // The pattern converting 'NOW IS THE TIME' to Wordsworth
final static int FAKE=2;   // A pseudo-random known test bit patterm
final static int RANDOM=3; // A random (based on Java's generator) bit patterm

public static final String BPtunny="/T3O9HNM4LRGIPCVEZDBSYFXAWJ5UQK8";
// The Bletchley Park (BP) symbols for Tunny, in binary order (0-31).
// '00000'=0='/' is invalid character
// N.B. No distinction made for figure shift or not
// 5 & 8 are also known as + & - (Move to figure shift/move to letter shift)
// These are the characters you read singly in a stream. Later analysis based
// on earlier shift characters is required to get the true message.

public static final String tunnyLetters="_T"+(char)0x0D+"O HNM"+(char)0x0A+"LRGIPCVEZDBSYFXAWJ\u9650UQK\u9660";
public static final String tunnyFigures="_5"+(char)0x0D+"9 £,."+(char)0x0A+")4@80:=3+_?'6%/-2\u2407\u965071(\u9660";
public static final String tunnyChars;  // Characters Tunny can encode

static {  tunnyChars=Codespace.unique(tunnyLetters+tunnyFigures); }

// Test strings
static String t1="NOW IS THE TIME FOR ALL GOOD MEN TO COME TO THE AID OF THE PARTY";
static String t2="I WANDERED LONELY AS A CLOUD THAT FLOATS ON HIGH OER VALES AND H";

Wheel [] chi  =new Wheel[5];
Wheel [] psi  =new Wheel[5];
Wheel [] motor=new Wheel[2];

// Wheel lengths; fixed machine design
static final int [] psiLen={43, 47, 51, 53, 59};  
static final int [] motorLen={37, 61}; // Appendix 10 of Colossus.  61 always moves, may move 37
static final int [] chiLen={41, 31, 29, 26, 23};    // Chi always moves

static final char SHIFT_UP  =(char)27;
static final char SHIFT_DOWN=(char)31;

// ----------------------------------------------------------------------
Tunny(int type) // Makes a test machine with known bit pattern
{ 
this(new int[5],new long[5],new int[2],new long[2],new int[5],new long[5]);

if (type==FAKE) {

  System.out.println("*** TEST MACHINE IN USE ***"); // Warn

  for (int i=0;i<psi.length;i++)    psi[i].pattern=fake(i);
  for (int i=0;i<motor.length;i++)  motor[i].pattern=fake(i);
  for (int i=0;i<chi.length;i++)    chi[i].pattern=fake(i);
}
else if (type==RANDOM) { // No warning, viewed as a valid machine

// Makes a random machine. w.l.g. can leave position=0

  for (int i=0;i<psi.length;i++)    psi[i].randomise();
  for (int i=0;i<motor.length;i++)  motor[i].randomise();
  for (int i=0;i<chi.length;i++)    chi[i].randomise();
}
else if (type==ZERO) {

  System.out.println("*** ZEROED MACHINE IN USE ***"); // Warn

  for (int i=0;i<psi.length;i++)    psi[i].pattern=0;
  for (int i=0;i<motor.length;i++)  motor[i].pattern=0;
  for (int i=0;i<chi.length;i++)    chi[i].pattern=0;

}
else if (type==POETRY) { // Setup to convert test phrase into poetry

  motor[0].pattern=(1L<<1);     // 0x00000....010 [bit 1 set]
  motor[1].pattern=(1L<<21);    // 0x000...1....0 [bit 21 set]

  for (int i=0;i<psi.length;i++)    psi[i].pattern=0;
  for (int i=0;i<chi.length;i++)    chi[i].pattern=0;

  for (int i=0;i<23;i++) { // 1st 23 chars mirror XOR, rest of Chi blank
    
    int from=tunnyLetters.indexOf(t1.charAt(i));
    int to  =tunnyLetters.indexOf(t2.charAt(i));
    int xor =from^to;
    for (int j=0;j<5;j++) 
      if ((xor & (1L<<j))!=0) 
        chi[4-j].pattern|=(1L<<i);    
  }

  for (int i=0;i<41;i++) { // Set Psi, aiming off for already set Chi
    
    int from=tunnyLetters.indexOf(t1.charAt(i+23));
    int to  =tunnyLetters.indexOf(t2.charAt(i+23));
    int xor =from^to;
    for (int j=0;j<5;j++) {
      int chiIndex=(i+23)%chiLen[4-j];  // Map us onto the relevant chi wheel
      boolean chiBits=((chi[4-j].pattern & (1L<<chiIndex))!=0); 
      if (((xor & (1L<<j))!=0) ^ chiBits) 
        psi[4-j].pattern|=(1L<<(i+1)); // +1 so that 1st is null
    }    
  }
}
}
// ----------------------------------------------------------------------
Tunny(int [] psiPos,long [] psiPatt,int [] motorPos,long [] motorPatt,
      int [] chiPos,long [] chiPatt) 
{ 
super(new Codespace(tunnyChars,tunnyChars,BPtunny,BPtunny));

for (int i=0;i<psiLen.length;i++)   psi[i]=new Wheel(0,psiPatt[i],psiLen[i]);
for (int i=0;i<motorLen.length;i++) motor[i]=new Wheel(0,motorPatt[i],motorLen[i]);
for (int i=0;i<chiLen.length;i++)   chi[i]=new Wheel(0,chiPatt[i],chiLen[i]);
} 
// ----------------------------------------------------------------------
static protected long fake(int i) {
// Make a 64 bit integer s.t. when i=0, every second bit is set
// when i=1, every third bit is set, etc
// For testing purposes - repeatable patterns, different for each wheel
long d=0L;
for (int j=0;j<64;j++) {
  if ((j%(i+2))==1) d|=1L;
  d<<=1;
}
return d;
}
// ----------------------------------------------------------------------
public String toString() 
{ 
StringBuilder sb=new StringBuilder(nL+"Chi wheels"+nL);
for (int i=0;i<5;i++)  sb.append(chi[i].toString());

sb.append("Motor wheels"+nL);
for (int i=0;i<2;i++)  sb.append(motor[i].toString());

sb.append("Psi wheels"+nL);
for (int i=0;i<5;i++)  sb.append(psi[i].toString());

return (this.getClass().getName()+" Cipher :  "+
       nL+sb.toString());} // Do not print codespace
// ----------------------------------------------------------
@Override
public String encode(String PT) 
{ 
return BPtoTeleprinter(process(insertShifts(PT)));
}
// ----------------------------------------------------------------------
String BPtoTeleprinter(String BP) {

// Converts the BP 'shiftless' interpretation into the actual teleprinter
// printout.  Result is shorter than the 'shiftless' interpretation
// as shifts are removed (counterintuitive)

StringBuilder sb=new StringBuilder(BP.length());
boolean shift=false;
for (int i=0;i<BP.length();i++) {
  switch(BP.charAt(i)) {
    case SHIFT_UP   : shift=true;  break;
    case SHIFT_DOWN : shift=false; break;
    default:
      int isAt=BPtunny.indexOf(BP.charAt(i));
      sb.append(shift?tunnyFigures.charAt(isAt)
                     :tunnyLetters.charAt(isAt));
  }
}
return sb.toString();
}
// ----------------------------------------------------------------------
String insertShifts(String PT) 
{
StringBuilder sb=new StringBuilder();
boolean shift=false;

// Convert into 5 bit chars, insert Letter/Figure shifts where required
for (int i=0;i<PT.length();i++) {
  if (cs.PTspace.indexOf(PT.charAt(i))==(-1)) throw new
    IllegalArgumentException(
          "Not a character Tunny can encode "+PT.substring(i,i+1));

  boolean needsShift;

  // We know it's a valid character, so must be in Figures or Letters
  // Determine if it is the opposite to current shift setting  
  if (shift) needsShift=tunnyFigures.indexOf(PT.charAt(i))==(-1);
  else       needsShift=tunnyLetters.indexOf(PT.charAt(i))==(-1);

  if (needsShift) { // Insert the shift character
    sb.append(shift?SHIFT_UP:SHIFT_DOWN);
    shift=!shift;
  }
  sb.append((char)(shift?tunnyFigures.indexOf(PT.charAt(i))
                        :tunnyLetters.indexOf(PT.charAt(i))));
}
return sb.toString();
}
// ----------------------------------------------------------------------
String stripShifts(String CT) 
{
return "DUMMY";
}
// ----------------------------------------------------------------------
private void printBin(int i) { // Bits that are 1/set print as 'x'

System.out.print(" "+cs.CTspace.charAt(i)+" ");
for (int j=0;j<5;j++)
  System.out.print(((i&(1<<(4-j)))!=0)?"x":".");
System.out.print(" ");
}
// ----------------------------------------------------------------------
private String process(String text) {  
// This is the actual (symmetric) encryption algorithm that acts on
// 5-bit characters (as a set of chars in a string). 

StringBuilder sb=new StringBuilder(text.length());

for (int i=0;i<text.length();i++) { 

  int chiMap=0;
  int psiMap=0;
  for (int j=0;j<5;j++) {  // Chapter 3 of Colossus confirms 1st is MSb
    if (chi[j].getSense()) chiMap|=(1<<(4-j));
    if (psi[j].getSense()) psiMap|=(1<<(4-j));
  
    chi[j].advance();
    if (motor[0].getSense())
      psi[j].advance();      // Psi advances based on Motor 0 (p48 and p407 of Colossus)
  }
  if (motor[1].getSense())
    motor[0].advance();      // Motor 0 advances based on Motor 1 (p48 and p407 of Colossus)
  motor[1].advance();        // Motor 1 always advances (ibid)

// ********* Limitations not implemented p49 of Colossus.. Only apply to SZ42A and SZ42B

  int result=((int)text.charAt(i)^chiMap^psiMap)&0x1F;
  
  sb.append(BPtunny.charAt(result));
}
return sb.toString(); 
}
// ----------------------------------------------------------------------
public void resetWheels() { 
// The problem with decoding a stream cipher with the machine that encoded
// it (e.g. for test purposes) is that wheels have moves on.  This resets them.
for (int i=0;i<psiLen.length;i++)   psi[i].position=0;
for (int i=0;i<motorLen.length;i++) motor[i].position=0;
for (int i=0;i<chiLen.length;i++)   chi[i].position=0;
}
// ----------------------------------------------------------------------
@Override
public String decode(String CT) { return encode(CT); }
// ----------------------------------------------------------
public static void main(String [] args) {

Tunny tunny=new Tunny(Tunny.POETRY);
System.out.println(tunny);

String CT=tunny.encode(t1);
System.out.println("Ciphertext : "+CT);

tunny.resetWheels();
System.out.println("Plaintext  : "+tunny.decode(CT));
}

// ---------------------------------------------------------------------
protected class Wheel
{
long pattern;  // Worst case is 61 bits, so long is safe
int position;
int size;
// ---------------------------------------------------------------------
Wheel(int position,long pattern,int size)
{
// A bit pattern.  On movement, move to next bit more significant than me
this.position=position%size;  
this.pattern=pattern;
this.size=size;
}
// ---------------------------------------------------------------------
void randomise()   { pattern=rand.nextLong()&((1L<<size)-1); }
// ---------------------------------------------------------------------
boolean getSense() { return (((pattern>>position)&1)!=0); }
// ---------------------------------------------------------------------
void advance()     { position=(position+1)%size; }
// ---------------------------------------------------------------------
public String toString()
{
StringBuilder sb=new StringBuilder(size+5);
for (int i=0;i<size;i++)
  sb.append(((pattern>>((i+position)%size)&1)==0)?'.':'x');

return "Wheel of size "+String.valueOf(size)+" "+sb.toString()+nL;
}
}
}