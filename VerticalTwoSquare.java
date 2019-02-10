public class VerticalTwoSquare extends ClassicalCipher
{
// Conducts TwoSquare encryption and decryption.
protected String keyT;
protected String keyB;

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

// ----------------------------------------------------------------------
VerticalTwoSquare(String wordT,String wordB) 
 { this(new Codespace(Codespace.StockAlphabet.MERGED_IJ),wordT,wordB); } 
// ----------------------------------------------------------------------
VerticalTwoSquare(Codespace cs,String wordT,String wordB)
{
super(cs);
if (cs.PTspace.length() != 25)
  throw new IllegalArgumentException("TwoSquare needs a 25 letter alphabet");

keyT=new Keyword().new Simple(cs,wordT).getKey();
keyB=new Keyword().new Simple(cs,wordB).getKey();
}
// ----------------------------------------------------------------------
public String toString() 
     { return ("Vertical Two Square Cipher : T Key "+keyT+" B Key "+keyB+nL+
                super.toString());}
// ----------------------------------------------------------------------
public String encode(String PT) 
{ 
String flat=(PT.length()%2==0)?cs.flattenToPT(PT):cs.flattenToPT(PT+"X");
StringBuilder sb=new StringBuilder();

for (int i=0;i<flat.length();i+=2) {

  int l1=keyT.indexOf(flat.charAt(i));
  int l2=keyB.indexOf(flat.charAt(i+1));

  int x1=l1%5;
  int y1=l1/5;
  int x2=l2%5;
  int y2=l2/5;

  if (x1==x2) { // Same column - transparency
    sb.append(flat.charAt(i));
    sb.append(flat.charAt(i+1));
  } else {
    sb.append(keyT.charAt(y1*5+x2));
    sb.append(keyB.charAt(y2*5+x1));
  }
}
return sb.toString(); 
}
// ----------------------------------------------------------------------
public String decode(String CT) 
{ 
String flat=cs.flattenToCT(CT);
StringBuilder sb=new StringBuilder();

for (int i=0;i<flat.length()-1;i+=2) {

  int l1=keyT.indexOf(flat.charAt(i));
  int l2=keyB.indexOf(flat.charAt(i+1));

  int x1=l1%5;
  int y1=l1/5;
  int x2=l2%5;
  int y2=l2/5;

  if (x1==x2) { // Same column - transparency
    sb.append(flat.charAt(i));
    sb.append(flat.charAt(i+1));
  } else {
    sb.append(keyT.charAt(y1*5+x2));
    sb.append(keyB.charAt(y2*5+x1));
  }
}
return sb.toString(); 
}
// ----------------------------------------------------------
public static void main(String [] args) {

// http://en.wikipedia.org/w/index.php?title=Two-square_cipher&oldid=562298993
VerticalTwoSquare twosquare=
   new VerticalTwoSquare(new Codespace(Codespace.StockAlphabet.NO_Q),"EXAMPLE","KEYWORD");

String target="HEDLXWSDJYANHOTKDG";
String PT="HELPMEOBIWANKENOBI";

//System.out.println(twosquare);
//System.out.println();

if (twosquare.knownTest(PT,target)) System.out.println("PASS");
else  System.out.println("***** FAIL *******");

}
}