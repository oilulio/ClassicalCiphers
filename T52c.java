public class T52c extends Sturgeon
{   
// Conduct Sturgeon T52c encryption and decryption.
// i.e. a Siemens and Halske T52c
// see https://en.wikipedia.org/w/index.php?title=Siemens_and_Halske_T52&oldid=887489719

/*
Copyright (C) 2020  S Combes

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

private String MKUkey;

static final int [] SRmaskC={0x251,0x138,0x28C,0x146,0x0A3,0x138,0x0B1,0x243,0x286,0x14C};  // Gets replaced in subclass
// SR05..01..SR10..SR06 (later <<=1 reverses order)

// ----------------------------------------------------------------------
T52c(String wheelSet,String wheelPos,String MKUkey) { 
super(wheelPos);
MKU=getMessageKeyUnit(MKUkey);

String [] ws=wheelSet.replace(" ","").split(":");
if (ws.length!=10) throw new IllegalArgumentException("Wheels key "+
    wheelSet+" does not have 10 items separted by ':'"); 
WSU=new int[10];

for (int i=0;i<10;i++) {
  boolean found=false;
  for (int j=0;j<5;j++) {
	if (ws[i].equals(ROMAN[j])) { WSU[i]=j+5; found=true; break; }
  }
  if (!found) {
	int wh=Integer.parseInt(ws[i]);
	if (wh<1 || wh>9 || wh%2!=1) throw new IllegalArgumentException(
	  "Invalid wheel, should be I,II,III,IV,IV,1,3,5,7 or 9");
    WSU[i]=wh/2;
  }
}
MKU_WSU=new int[10];
for (int i=0;i<10;i++) MKU_WSU[i]=WSU[MKU[i]];  

scrambler=new Scrambler(FIXED_PAIRS);

SRmask=SRmaskC;
}
// ----------------------------------------------------------------------
T52c(String wheelSet,int [] wheelPos,String MKUkey) { 
super(wheelPos);
MKU=getMessageKeyUnit(MKUkey);

String [] ws=wheelSet.replace(" ","").split(":");
if (ws.length!=10) throw new IllegalArgumentException("Wheels key "+
    wheelSet+" does not have 10 items separted by ':'"); 
WSU=new int[10];

for (int i=0;i<10;i++) {
  boolean found=false;
  for (int j=0;j<5;j++) {
	if (ws[i].equals(ROMAN[j])) { WSU[i]=j+5; found=true; break; }
  }
  if (!found) {
	int wh=Integer.parseInt(ws[i]);
	if (wh<1 || wh>9 || wh%2!=1) throw new IllegalArgumentException(
	  "Invalid wheel, should be I,II,III,IV,IV,1,3,5,7 or 9");
    WSU[i]=wh/2;
  }
}
MKU_WSU=new int[10];
for (int i=0;i<10;i++) MKU_WSU[i]=WSU[MKU[i]];  

scrambler=new Scrambler(FIXED_PAIRS);
}
// ----------------------------------------------------------------------
protected int [] getMessageKeyUnit(String mkuKey) { 
// Same effective function as WheelSettingUnit (perm of 10 inputs) but
// is changed message-by-message not day-by-day.
this.MKUkey=new String(mkuKey); // Only for toString()
int [] movesto=new int[10];

final String MKUKEY_SET="PSTUWXYZ";
boolean [] switches=new boolean[15]; // 15 switches (label 0-14, not per docs)
int [] links={0x1D4B27,0x3A964E,0xE85A39,0xA369E4,0x8EA593};

for (int i=0;i<10;i++) movesto[i]=i; // initially
for (int i=0;i<5;i++) {
  int thisState=7-MKUKEY_SET.indexOf(mkuKey.charAt(i));  // Bits are ordered in reverse
  if (thisState>7)
	throw new IllegalArgumentException("MKU Key contains character other than "+MKUKEY_SET);

  switches[i+ 0]=(((1<<(thisState+16))&links[i])!=0);
  switches[i+ 5]=(((1<<(thisState+ 8))&links[i])!=0);
  switches[i+10]=(((1<<(thisState+ 0))&links[i])!=0);  
}
for (int sw=0;sw<5;sw++) {   // Bank 1
  int a=sw*2;
  int b=sw*2+1;
  if (switches[sw]) { int tmp=movesto[a];movesto[a]=movesto[b];movesto[b]=tmp; }
}
for (int sw=5;sw<10;sw++) {  // Bank 2
  int a=(sw-5)*2+1;
  int b=((sw-4)*2)%10;
  if (switches[sw]) { int tmp=movesto[a];movesto[a]=movesto[b];movesto[b]=tmp; }
}
for (int sw=10;sw<15;sw++) { // Bank 3
  int a=sw-10;
  int b=sw-5;
  if (switches[sw]) { int tmp=movesto[a];movesto[a]=movesto[b];movesto[b]=tmp; }
}
int [] mapping=new int[10];
for (int i=0;i<10;i++) mapping[movesto[i]]=i;

return mapping;
}
// ----------------------------------------------------------------------
@Override
public String toString() {

StringBuilder sb=new StringBuilder(200);
sb.append("Key : Wheel Settings ");
for (int i=0;i<10;i++) sb.append(LABELS[(WSU[i]+5)%10]+((i<9)?":":""));
sb.append(" Wheel Positions ");
for (int i=0;i<10;i++) sb.append((wheels[i].position+1)+((i<9)?":":" "));

sb.append("Message Key = "+MKUkey);

return super.toString()+sb.toString()+nL; 
} 
// ----------------------------------------------------------
public static void main(String [] args) {

// Comparison with MTC3 JAR File
String PT="THERE9IS9NOTHING9EITHER9GOOD9OR9BAD9BUT9THINKING9MAKES9IT9SO";
String CT="JKCZJPRZPKS9+XRB3UKCZMH3JSIKYDZBURXYB8JB3/RH+AA4M3QK4NFKJWSL";

T52c t52c=new T52c("I:9:II:7:III:5:IV:3:V:1","01:02:03:04:05:06:07:08:09:10","TXYSP");

boolean pass=t52c.knownTestEncode(PT,CT);
t52c=new T52c("I:9:II:7:III:5:IV:3:V:1","01:02:03:04:05:06:07:08:09:10","TXYSP");
pass&=t52c.knownTestDecode(PT,CT);

if (pass) System.out.println("PASS");
else System.out.println("******** FAIL ***********");

}
}
