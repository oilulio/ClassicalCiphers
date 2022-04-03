/*
Copyright (C) 2021  S Combes

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

public class Sigaba_CSP2900 extends Sigaba {

// Sigaba CSP2900 variant.

Sigaba_CSP2900(boolean mimicMTC3_LASRY,
       int [] cipherOrder, int [] controlOrder, int [] indexOrder,
       int [] cipherOffset,int [] controlOffset,int [] indexOffset,
       boolean [] cipherRev, boolean [] controlRev) { 
	   
super(false,mimicMTC3_LASRY,
       cipherOrder,controlOrder,indexOrder,
       cipherOffset,controlOffset,indexOffset,
       cipherRev,controlRev);
// These 3 line are the differences from a CSP889
indexMapping=new int []{9,1,2,3,3,4,4,4,5,5,5,6,6,6,6,10,10,10,7,7,0,0,8,8,8,8}; // 10 is a dump value
indexInput=new int[]{'D'-'A','E'-'A','F'-'A','G'-'A','H'-'A','I'-'A'}; // Now uses 6 inputs
reverseStep=new boolean []{false,true,false,true,false};  // 2nd and 4th rotors reversed for CSP2900
}
//-------------------------------------------------------------------------------
public static Sigaba_CSP2900 deterministicFactory(boolean MTC3_LASRY,
    String PT,boolean [] cipherRev,String init) {

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

return new Sigaba_CSP2900(MTC3_LASRY,cipherOrder, controlOrder, indexOrder,
                         cipherOffset,controlOffset,indexOffset,
                         cipherRev,   controlRev);

}
//-------------------------------------------------------------------------------
public static Sigaba_CSP2900 randomFactory(boolean MTC3_LASRY) {  // Give me a random machine

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

return new Sigaba_CSP2900(MTC3_LASRY,cipherOrder, controlOrder, indexOrder,
                         cipherOffset,controlOffset,indexOffset,
                         cipherRev,   controlRev);
}
//-------------------------------------------------------------------------------
public static Sigaba_CSP2900 statedFactory(boolean MTC3_LASRY,String ipkey) {
	
ipkey=ipkey.replace(" ","");
// From Stamp's format "987601234501243 1100001010 AAABBCCCDD98703"	
String wheels=ipkey.substring(0,10);
String indexW=ipkey.substring(10,15);

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
  cipherOffset[i]=(ipkey.charAt(25+i))-'A';
  controlOffset[i]=(ipkey.charAt(30+i))-'A';
  indexOffset[i]=(ipkey.charAt(35+i))-'0';
  cipherRev[i]=(ipkey.charAt(15+i))=='1';
  controlRev[i]=(ipkey.charAt(20+i))=='1';;
}

return new Sigaba_CSP2900(MTC3_LASRY,cipherOrder, controlOrder, indexOrder,
                         cipherOffset,controlOffset,indexOffset,
                         cipherRev,   controlRev);
}
//-------------------------------------------------------------------------------
public static void main(String [] args) {

boolean passed=true;

// Encryption is "Sigaba 987601234501243 1100001010 AAABBCCCDD98703"

// Cross check with encryption by Lasry code (not a MTC3 challenge, but uses same PT)

String LasryPT="AH FOUL SHREWD NEWS BESHREW THY VERY HEART I DID NOT THINK TO BE SO SAD TONIGHT AS THIS HATH MADE ME";
String LasryCT="HZCPBIZSQNHYMGHHHYKKDFGHVWEALKQMUOYWCUAKZZGVAEWSYOUJFXBYRYZEBXYKWGXTHCAZOIQYLFXCNZVUFNPNSYZVMZCBXHOA";

Sigaba_CSP2900 sigaba=Sigaba_CSP2900.statedFactory(true,"89145 36702 23410 00110 01010 HYEXA OMFOG 06061");

passed&=sigaba.decode(LasryCT).equals(LasryPT);  

sigaba.reset();

passed&=sigaba.encode(LasryPT).equals(LasryCT);

if (passed) System.out.println("PASS");
else        System.out.println("*** FAIL ***");
}
}