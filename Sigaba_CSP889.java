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

public class Sigaba_CSP889 extends Sigaba {

// Sigaba M-134-C/CSP889 variant.  Default case of superclass

// Tested against 100,000 encrypts/decrypts of random 800 character texts against the Lasry 
// code in the MTC3 "The SIGABA Challenge Part 1" (as adjusted for the z<->space difference)

Sigaba_CSP889(boolean mimicMTC3_STAMP, boolean mimicMTC3_LASRY,
       int [] cipherOrder, int [] controlOrder, int [] indexOrder,
       int [] cipherOffset,int [] controlOffset,int [] indexOffset,
       boolean [] cipherRev, boolean [] controlRev) { 
	   
super(mimicMTC3_STAMP,mimicMTC3_LASRY,
       cipherOrder,controlOrder,indexOrder,
       cipherOffset,controlOffset,indexOffset,
       cipherRev,controlRev);
}

//-------------------------------------------------------------------------------
public static Sigaba_CSP889 deterministicFactory(boolean MTC3_STAMP,boolean MTC3_LASRY,
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

return new Sigaba_CSP889(MTC3_STAMP,MTC3_LASRY,cipherOrder, controlOrder, indexOrder,
                         cipherOffset,controlOffset,indexOffset,
                         cipherRev,   controlRev);

}	   
//-------------------------------------------------------------------------------
public static Sigaba_CSP889 randomFactory(boolean MTC3_STAMP,boolean MTC3_LASRY) {  // Give me a random machine

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

return new Sigaba_CSP889(MTC3_STAMP,MTC3_LASRY,cipherOrder, controlOrder, indexOrder,
                         cipherOffset,controlOffset,indexOffset,
                         cipherRev,   controlRev);


}
//-------------------------------------------------------------------------------
public static Sigaba_CSP889 statedFactory(boolean MTC3_STAMP,boolean MTC3_LASRY,String ipkey) {
	
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

return new Sigaba_CSP889(MTC3_STAMP,MTC3_LASRY,cipherOrder, controlOrder, indexOrder,
                         cipherOffset,controlOffset,indexOffset,
                         cipherRev,   controlRev);
}
//-------------------------------------------------------------------------------
public static void main(String [] args) {

boolean passed=true;

// Test from MTC3 STAMP challenge additional files
String StampCT="SGYRGRHCQQXBBMBLDCFUJNEEEBPGNYCZYOWZYBORMREBQBGMJWQURFQQAOIKGNNOCSFRLWUADGLUMEQIDDEWAEEDCYJJINVXDJCHODWRAOJSINFGAPTRUUNYTQVBZTTABDWZNMAVEEOK";
String StampPT="AD HOC AD LOC QUID PRO QUO SO LITTLE TIME SO MUCH TO KNOW FOUR SCORE AND SEVEN YEARS AGO SPACE THE FINAL FRONTIER IN THE BEGINNING BUZZ BUZZ";
// Encryption is "Sigaba_CSP889 987601234501243 1100001010 AAABBCCCDD98703 0 plain.txt cipher.txt"

Sigaba_CSP889 sigaba=Sigaba_CSP889.statedFactory(true,false,"98760 12345 01243 11000 01010 AAABB CCCDD 98703");
passed&=sigaba.decode(StampCT).equals(StampPT.replace("Z","X"));  // Any Zs will have corrupted into Xs

sigaba.reset();

passed&=sigaba.encode(StampPT).equals(StampCT);

// Comparison with results of Lasry code
String LasryCT="JTSCALXDRWOQKRXHKMVD";
String LasryPT="AAAAAAAAAAAAAAAAAAAA";

sigaba=Sigaba_CSP889.statedFactory(false,true,"01234 56789 01234 10001 00100 ABCDE FGHIJ 01234");
      
passed&=sigaba.decode(LasryCT).equals(LasryPT);  

sigaba.reset();

passed&=sigaba.encode(LasryPT).equals(LasryCT);

if (passed) System.out.println("PASS");
else        System.out.println("*** FAIL ***");

}
}
