public class T52ab extends Sturgeon
{   
// Conduct Sturgeon T52ab encryption and decryption.
// i.e. a Siemens and Halske T52a or T52b (cryptographically identical)
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

int [][] pairs=new int[5][2];

T52ab(int [] wheelPos) { super(wheelPos); } // Only for T52d passthrough	
T52ab(String wheelPos) { super(wheelPos); } // Only for T52d passthrough	
// ----------------------------------------------------------------------
T52ab(String wheelSet,String wheelPos) { 

// Wheelset is a string of 10 elements separated by ":"
// The elements define which wheels connect with which elements, respectively from A-K
// i.e. the first element is paired with wheel A, etc
// If an element is of Roman form (I,II,III...V)  it indicates an XOR control
// If an element is of Latin form a-b, e.g. 1-2, it indicates a permutation link.

super(wheelPos);

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
// ----------------------------------------------------------
@Override
public String toString() { 

StringBuilder sb=new StringBuilder(200);

sb.append("I-V are XOR, 1-9 are Permutation control"+nL);

sb.append(nL+"Key : Wheel Settings ");
for (int i=0;i<10;i++) {
  if (WSU[i]<5) sb.append(LABELS[WSU[i]]);                   // XOR
  else sb.append(scrambler.pairs[WSU[i]-5][0]+"-"+scrambler.pairs[WSU[i]-5][1]); // Perm
  if (i<9) sb.append(":");
}
sb.append(" Wheel Positions ");
for (int i=0;i<10;i++) sb.append((wheels[i].position+1)+((i<9)?":":""));

return super.toString()+sb.toString()+nL; 
} 
// ----------------------------------------------------------
public static void main(String [] args) {

// Comparison with MTC3 JAR File
String PT="ALL9THE9WORLDS9A9STAGE9AND9ALL9THE9MEN9AND9WOMEN9MERELY9PLAYERS9THEY9HAVE9THEIR9EXITS9AND9THEIR9ENTRANCES9AND9ONE9MAN9IN9HIS9TIME9PLAYS9MANY9PARTS";
String CT="ESCX+HCWYDDXOKE4JT3BCFV4KOHBNO4QDGB4AWVMH/XSFRTTAHG/4U/ANC9O3ZJCPFCSINLEEJWVLNVNP/GI9/MOQWA3ZVLHBCG3PVQZAYTPHGVNZE/MIVDPPFG/43C9FKJAYGCKHQEREXUF4Z";

String wheelPos="53:14:68:36:64:01:01:01:01:01";

T52ab t52ab=new T52ab("IV:5-8:I:II:2-9:V:3-10:4-7:1-6:III",wheelPos);

boolean pass=t52ab.knownTestEncode(PT,CT);
t52ab=new T52ab("IV:5-8:I:II:2-9:V:3-10:4-7:1-6:III",wheelPos);
pass&=t52ab.knownTestDecode(PT,CT);

if (pass) System.out.println("PASS");
else System.out.println("******** FAIL ***********");
}
}
