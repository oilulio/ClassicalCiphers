public class T52e extends T52d
{
// Conduct Sturgeon T52e encryption and decryption.
// i.e. a Siemens and Halske T52e
// see https://en.wikipedia.org/w/index.php?title=Siemens_and_Halske_T52&oldid=887489719

/*
Copyright (C) 2020-2022  S Combes

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
// Only difference from T52d is addition of SR logic, and use of standard permutation

// ----------------------------------------------------------------------
T52e(String wheelSet,String wheelPos,boolean KTF) { 
super(wheelPos,KTF);
// Below repeat code mostly from T52c (not in inheritance path) less MKU

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
MKU_WSU=WSU; // MKU is null by default.  So combination is same as WSU.

scrambler=new Scrambler(FIXED_PAIRS);
}
// ----------------------------------------------------------------------
@Override
protected int SRlogic(int input) { // Repeat of T52c code, as does not inherit

int [] SRmask={0x059,0x047,0x28C,0x1E0,0x283,0x138,0x2B0,0x123,0x216,0x14C};
// SR05..01..SR10..SR06 (later <<=1 reverses order)

int output=0;
for (int i=0;i<10;i++) {
  output<<=1;
  output|=(Integer.bitCount(input&SRmask[i])&1);
}
return output;
}
// ----------------------------------------------------------------------
@Override
public String toString() {

StringBuilder sb=new StringBuilder(200);
sb.append(wheelData());
sb.append(nL+"Key : Wheel Settings ");
for (int i=0;i<10;i++) sb.append(LABELS[(WSU[i]+5)%10]+((i<9)?":":""));
sb.append(" Wheel Positions ");
for (int i=0;i<10;i++) sb.append((wheels[i].position+1)+((i<9)?":":" "));

return sb.toString()+nL+"KTF="+(KTF?"ON":"OFF")+nL; 
} 
// ----------------------------------------------------------------------
public static void main(String [] args) {

// Comparison with MTC3 JAR File
String PT="STRICKEN9I9LURCHED9FORTH9IN9SEARCH9OF9AID9BUT9FINDING9ONLY9SLACK9JAWED9GAWKERS9I9IMMEDIATELY9GAVE9UP9HOPE9AND9I9COLLAPSED9ONTO9THE9SUNDIAL";
String CT="GENVJENDRR4X+CVF9BHIB48YYIMKBV/JYTBM3E+VMDL4YVZHWDOUMJQ+SIO4PQPPSUNAANRZ/PFLK83OZSLCHVQIQFYXYI+O8NTOTDC+8NBMFVCJYG8EDSHUANR33AH4KME8COEXL3";

String wheelPos="50:41:45:13:49:06:30:25:48:01";
T52e t52e=new T52e("V:II:1:I:7:5:IV:9:3:III",wheelPos,false);
boolean pass=t52e.knownTestEncode(PT,CT);

t52e=new T52e("V:II:1:I:7:5:IV:9:3:III",wheelPos,false);
pass&=t52e.knownTestDecode(PT,CT);

CT="GJCFHHVHHUODNHSWKBPLE+UPBVKXM8X8SUII9BWFPXJ9A+LNDGHG9NKLZ/G9GT3WC9/I8SAGYCJNGCRN+OGGTABQSFEFNUFTAIS+D9M38IIAX84EBTIKVISSKRSJZA3XKDY83V8TD8";

t52e=new T52e("V:II:1:I:7:5:IV:9:3:III",wheelPos,true);

pass&=t52e.knownTestEncode(PT,CT);

t52e=new T52e("V:II:1:I:7:5:IV:9:3:III",wheelPos,true);
pass&=t52e.knownTestDecode(PT,CT);

if (pass) System.out.println("PASS");
else System.out.println("******** FAIL ***********");
}
}
