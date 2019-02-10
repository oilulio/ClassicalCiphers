import java.io.*;

public class DoublePlayfair extends ClassicalCipher
{
// Conducts DoublePlayfair encryption and decryption.
// Not subclass of Playfair as they aren't that similar - closer to
// HorizontalTwoSquare, but has seriation, a different rule for
// letters on same row, and does the encryption twice
// (although Wikipedia has DoublePlayfair=TwoSquare)
// This method from http://www.pbs.org/wgbh/nova/decoding/doubplayfair.html

// "Jim Gillogly has noted that declassified NSA documents refer to 
// another cipher of this type, in which the digraphs were enciphered
// twice by means of the two-square cipher. Since each letter enciphered 
// the first time was then found in the square on the other side for
// the second encipherment, the relation between plain and cipher 
// digraphs was much more complicated than in regular Playfair"

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

protected String keyL;
protected String keyR;
protected int rank;

// ----------------------------------------------------------------------
DoublePlayfair(String wordL,String wordR,int rank) 
   { this(new Codespace(Codespace.StockAlphabet.MERGED_IJ),wordL,wordR,rank); } 
// ----------------------------------------------------------------------
DoublePlayfair(Codespace cs,String wordL,String wordR,int rank)
{
super(cs);
if (cs.PTspace.length() != 25)
  throw new IllegalArgumentException("DoublePlayfair needs a 25 letter alphabet");

keyL=new Keyword().new Simple(cs,wordL).getKey();
keyR=new Keyword().new Simple(cs,wordR).getKey();
this.rank=rank;
}
// ----------------------------------------------------------------------
public String toString() 
     { return ("Double Playfair Cipher : L Key "+keyL+" R Key "+keyR+
               "Seriation = "+rank+nL+
                super.toString());}
// ----------------------------------------------------------------------
@Override
public String encode(String PT) 
{ 
String flat=(PT.length()%2==0)?cs.flattenToPT(PT):cs.flattenToPT(PT)+"X";
flat=seriateOn(flat,rank);

StringBuilder sb=new StringBuilder(PT.length());

for (int i=0;i<flat.length();i+=2) 
  sb.append(singleEncode(singleEncode(flat.substring(i,i+2))));

return sb.toString(); 
}
// ----------------------------------------------------------------------
protected String singleEncode(String text) 
{
StringBuilder sb=new StringBuilder(2);

int l1=keyL.indexOf(text.charAt(0));
int l2=keyR.indexOf(text.charAt(1));

int x1=l1%5;
int y1=l1/5;
int x2=l2%5;
int y2=l2/5;

if (y1==y2) { // Same row - letters to left
  sb.append(keyR.charAt(y1*5+(x2+4)%5));
  sb.append(keyL.charAt(y1*5+(x1+4)%5));
} else {
  sb.append(keyR.charAt(y1*5+x2));
  sb.append(keyL.charAt(y2*5+x1));
}
return sb.toString();
}
// ----------------------------------------------------------------------
@Override
public String decode(String CT) 
{ 
String flat=cs.flattenToCT(CT);

StringBuilder sb=new StringBuilder(CT.length());

for (int i=0;i<flat.length()-1;i+=2)
  sb.append(singleDecode(singleDecode(flat.substring(i,i+2))));

return seriateOff(sb.toString(),rank); 
}
// ----------------------------------------------------------------------
protected String singleDecode(String text) 
{
StringBuilder sb=new StringBuilder(2);

int l1=keyR.indexOf(text.charAt(0));
int l2=keyL.indexOf(text.charAt(1));

int x1=l1%5;
int y1=l1/5;
int x2=l2%5;
int y2=l2/5;

if (y1==y2) { // Same row - letters to right
  sb.append(keyL.charAt(y1*5+(x2+1)%5));
  sb.append(keyR.charAt(y1*5+(x1+1)%5));
} else {
  sb.append(keyL.charAt(y1*5+x2));
  sb.append(keyR.charAt(y2*5+x1));
}
return sb.toString(); 
}
// ----------------------------------------------------------
public static void main(String [] args) {

// From http://www.pbs.org/wgbh/nova/decoding/doubplayfair.html

DoublePlayfair dp=new DoublePlayfair("HAMBURG","NLIHGEMXVFWPZUDYQSTCORKAB",7);

String target="MPSRHRMXNWAKBWMYWEBICWSP";
String PT="MYHOVERCRAFTISFULLOFEELS";

if     (dp.knownTest(PT,target)) System.out.println("PASS");
else   System.out.println("***** FAIL *******");

}
}