public class T52d extends T52ab
{
// Conduct Sturgeon T52d encryption and decryption.
// i.e. a Siemens and Halske T52d
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
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
protected boolean KTF; // Klartext function - feedback from cleartext into stepping
protected static int A=0; 
protected static int B=1; 
protected static int C=2; 
protected static int D=3; 
protected static int E=4; 
protected static int F=5; 
protected static int G=6; 
protected static int H=7; 
protected static int J=8; 
protected static int K=9;

// ----------------------------------------------------------------------
T52d(String wheelSet,String wheelPos,boolean KTF) { 
  super(wheelSet,wheelPos);
  this.KTF=KTF;
}
// ----------------------------------------------------------------------
T52d(int [] wheelPos,boolean KTF) { 
// This constructor only exists for subclass passthrough (specifically T52e)
// Consequence of forbidden multiple inheritance.
  super(wheelPos);
  if (this.getClass().getName().equals("T52d")) throw new IllegalArgumentException(
    "Cannot construct T52d without wheel settings");
  this.KTF=KTF;
}
// ----------------------------------------------------------------------
T52d(String wheelPos,boolean KTF) { 
// This constructor only exists for subclass passthrough (specifically T52e)
// Consequence of forbidden multiple inheritance.
  super(wheelPos);
  if (this.getClass().getName().equals("T52d")) throw new IllegalArgumentException(
    "Cannot construct T52d without wheel settings");
  this.KTF=KTF;
}
// ----------------------------------------------------------------------
T52d(String wheelSet,int [] wheelPos,boolean KTF) { // copied for now, inherit later

// Wheelset is a string of 10 elements separated by ":"
// The elements define which wheels connect with which elememts, respectively from A-K
// i.e. the first element is paired with wheel A, etc
// If an element is of Roman form (I,II,III...V)  it indicates an XOR control
// If an element is of Latin form a-b, e.g. 1-2, it indicates a permutation link.

super(wheelPos);
this.KTF=KTF;

String [] ws=wheelSet.replace(" ","").split(":");
pairs=new int[5][2];
int pair=0;
WSU=new int[10];

for (int i=0;i<10;i++) {
  if (ws[i].indexOf("-")==-1) { // Should therefore be ROMAN
	boolean found=false;
    for (int j=0;j<5;j++) {
	  if (ws[i].equals(ROMAN[j])) { WSU[i]=j; found=true; break; }
	}
	if (!found) throw new IllegalArgumentException("Roman wheel not matched");
  } else {
	String [] nos=ws[i].split("-");
	WSU[i]=pair+5;
	pairs[pair  ][0]=Integer.parseInt(nos[0]); // May throw illegal argument
    pairs[pair++][1]=Integer.parseInt(nos[1]);
  }
}
MKU_WSU=WSU; // MKU is null by default.  So combination is same as WSU.
scrambler=new Scrambler(pairs);
}
// ----------------------------------------------------------------------
@Override
public String toString() { return super.toString()+"KTF="+(KTF?"ON":"OFF")+nL; } 
// ----------------------------------------------------------------------
// Difference from T52ab is irregular wheel stepping (including KTF)
// ----------------------------------------------------------------------
@Override
protected boolean [] toAdvance(int pt) {

boolean [] advance=new boolean[10];
boolean z=((pt&(1<<2))!=0);
  
if (KTF) {
  advance[A]=( z|| wheels[B].getOffsetSense() || wheels[C].getOffsetSense());
  advance[B]=( z||!wheels[C].getOffsetSense() || wheels[D].getOffsetSense());
  advance[C]=(    !wheels[D].getOffsetSense() || wheels[E].getOffsetSense());
  advance[D]=(    !wheels[E].getOffsetSense() ||!wheels[F].getOffsetSense());
  advance[E]=(!z|| wheels[F].getOffsetSense() ||!wheels[G].getOffsetSense());
  advance[F]=(!z|| wheels[G].getOffsetSense() || wheels[H].getOffsetSense());
  advance[G]=(    !wheels[H].getOffsetSense() ||!wheels[J].getOffsetSense());
  advance[H]=(     wheels[J].getOffsetSense() ||!wheels[K].getOffsetSense());
  advance[J]=(     wheels[K].getOffsetSense() ||!wheels[A].getOffsetSense());
  advance[K]=(     wheels[A].getOffsetSense() ||!wheels[B].getOffsetSense());
} else {
  advance[A]=!(wheels[E].getOffsetSense() && wheels[F].getOffsetSense());
  advance[B]=advance[A];
  advance[C]=advance[A];
  advance[D]=advance[A];
  advance[E]= (wheels[F].getOffsetSense() ||!wheels[G].getOffsetSense());
  advance[F]= (wheels[G].getOffsetSense() || wheels[H].getOffsetSense());
  advance[G]=!(wheels[H].getOffsetSense() && wheels[J].getOffsetSense());
  advance[H]= (wheels[J].getOffsetSense() ||!wheels[K].getOffsetSense());
  advance[J]= (wheels[K].getOffsetSense() ||!wheels[A].getOffsetSense());
  advance[K]=(!wheels[D].getOffsetSense() || wheels[E].getOffsetSense());
}
return advance;
}
// ----------------------------------------------------------
public static void main(String [] args) {

// Comparison with MTC3 JAR File
String PT="BE9CAREFUL9WHEN9WE9CAPTURE9HIM9WE9CANNOT9CLAIM9THE9REWARD9UNLESS9WE9HAVE9FIFTYONE9PERCENT9OF9THE9CARCASS";
String CT="W8OTY9AKCTH94KLMFLUVG8D4LBI+SAFXQAT9QMZ4WE3VPTM/HV9HPPNCY+LB8PT/GMWW8IZVFFDPJ9A4VN3ELDFP3HWXWKJYXIAPY4BB";

String wheelPos="05:16:61:11:14:06:11:55:10:26";
T52d t52d=new T52d("III:7-10:I:2-6:V:5-9:3-4:II:IV:1-8",wheelPos,false);

boolean pass=t52d.knownTestEncode(PT,CT);

t52d=new T52d("III:7-10:I:2-6:V:5-9:3-4:II:IV:1-8",wheelPos,false);
pass&=t52d.knownTestDecode(PT,CT);

CT="W8OFOKTBGYZEILQ+RITJWFYANU4PK48VII3GBLL/YWDG8HTZTPCTBBZAGX3JPOEBWCICDNF+P4+W3ZRVYXGHQKXZLX9PTDJ8AN4LUMLM";

t52d=new T52d("III:7-10:I:2-6:V:5-9:3-4:II:IV:1-8",wheelPos,true);

pass&=t52d.knownTestEncode(PT,CT);

t52d=new T52d("III:7-10:I:2-6:V:5-9:3-4:II:IV:1-8",wheelPos,true);
pass&=t52d.knownTestDecode(PT,CT);

if (pass) System.out.println("PASS");
else System.out.println("******** FAIL ***********");
}
}
