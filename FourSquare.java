public class FourSquare extends ClassicalCipher
{
// Conducts FourSquare encryption and decryption.
protected String keyTR;
protected String keyBL;

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
FourSquare(String wordTR,String wordBL) 
        { this(new Codespace(Codespace.StockAlphabet.MERGED_IJ),wordTR,wordBL); } 
// ----------------------------------------------------------------------
FourSquare(Codespace cs,String wordTR,String wordBL)
{
super(cs);
if (cs.PTspace.length() != 25)
  throw new IllegalArgumentException("FourSquare needs a 25 letter alphabet");

keyTR=new Keyword().new Simple(cs,wordTR).getKey();
keyBL=new Keyword().new Simple(cs,wordBL).getKey();
}
// ----------------------------------------------------------------------
public String toString() 
     { return (this.getClass().getName()+" Cipher : TR Key "+keyTR+" BL Key "+keyBL+nL+
                super.toString());}
// ----------------------------------------------------------------------
@Override
public String encode(String PT) 
{ 
String flat=(PT.length()%2==0)?cs.flattenToPT(PT):cs.flattenToPT(PT)+"X";
StringBuilder sb=new StringBuilder();

for (int i=0;i<flat.length();i+=2) {

  int l1=cs.PTspace.indexOf(flat.charAt(i));
  int l2=cs.PTspace.indexOf(flat.charAt(i+1));

  int x1=l1%5;
  int y1=l1/5;
  int x2=l2%5;
  int y2=l2/5;

  sb.append(keyTR.charAt(y1*5+x2));
  sb.append(keyBL.charAt(y2*5+x1));
}
return sb.toString(); 
}
// ----------------------------------------------------------------------
@Override
public String decode(String CT) 
{ 
String flat=cs.flattenToCT(CT);
StringBuilder sb=new StringBuilder();

for (int i=0;i<flat.length();i+=2) {

  int l1=keyTR.indexOf(flat.charAt(i));
  int l2=keyBL.indexOf(flat.charAt(i+1));

  int x1=l1%5;
  int y1=l1/5;
  int x2=l2%5;
  int y2=l2/5;

  sb.append(cs.PTspace.charAt(y1*5+x2));
  sb.append(cs.PTspace.charAt(y2*5+x1));
}
return sb.toString(); 
}
// ----------------------------------------------------------
public static void main(String [] args) {

// http://en.wikipedia.org/w/index.php?title=Four-square_cipher&oldid=565762600
FourSquare foursquare=new FourSquare(new Codespace(Codespace.StockAlphabet.NO_Q),"EXAMPLE","KEYWORD");

String target="FYGMKYHOBXMFKKKIMD";
String PT="HELPMEOBIWANKENOBI";

if (foursquare.knownTest(PT,target)) System.out.println("PASS");
else  System.out.println("***** FAIL *******");
}
}