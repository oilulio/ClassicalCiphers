import java.util.Date;
import java.util.*;

public class Playfair extends ClassicalCipher
{
// Conducts Playfair encryption and decryption.
protected String key;

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
Playfair(String word) 
     { this(new Codespace(Codespace.StockAlphabet.MERGED_IJ),word); } 
// ----------------------------------------------------------------------
Playfair(Codespace cs,String word)
{
super(cs);
if (cs.PTspace.length() != 25)
  throw new IllegalArgumentException("Playfair needs a 25 letter alphabet");

key=new Keyword().new Simple(cs,word).getKey();
}
// ----------------------------------------------------------------------
public String toString() 
         { return ("Playfair Cipher : Key "+key+nL+super.toString());}
// ----------------------------------------------------------------------
boolean validCT(String CT)
{ // Does this meet the rules to be Playfair CT?
// For Playfair, there should be no duplicates in the pairs starting
// at 0,2.. etc.  Should (but won't insist) also be an even number of letters
for (int i=0;i<(CT.length()-1)/2;i++)
  if (CT.charAt(i*2)==CT.charAt(i*2+1))
    return false;
return true;
}
// ----------------------------------------------------------
@Override
public String encode(String PT) 
{ 
String flat=cs.flattenToPT(PT);

StringBuilder sb=new StringBuilder();
sb.append(flat.charAt(0));
for (int i=1;i<flat.length();i++) {
  if ((i%2)==1 && flat.charAt(i)==flat.charAt(i-1)) sb.append("X");
  sb.append(flat.charAt(i));
}
if (sb.length()%2!=0) sb.append("X"); // Even up

flat=sb.toString();  // The new PT, flattened, with pairs separated
sb=new StringBuilder();

for (int i=0;i<flat.length();i+=2) {

  int l1=key.indexOf(flat.charAt(i));
  int l2=key.indexOf(flat.charAt(i+1));

  int x1=l1%5;
  int y1=l1/5;
  int x2=l2%5;
  int y2=l2/5;

  if (x1==x2) { // Column.  Pick letters below
    sb.append(key.charAt((l1+5)%25));
    sb.append(key.charAt((l2+5)%25));
  } 
  else if (y1==y2) { // Row. Pick letters to right
    sb.append(key.charAt(y1*5+(x1+1)%5));
    sb.append(key.charAt(y2*5+(x2+1)%5));
  } 
  else { // Rectangle.  Pick opposite corner of same row
    sb.append(key.charAt(y1*5+x2));
    sb.append(key.charAt(y2*5+x1));
  }
}
return sb.toString(); 
}
// ----------------------------------------------------------------------
@Override
public String decode(String CT) 
{ 
String flat=cs.flattenToCT(CT);
boolean inferredLast=false;

if ((flat.length()%2)==1) { // Possble to infer the last letter
// even if text had been trimmed to original text length (i.e.
// sacrificing end of last bigram)
  inferredLast=true;
  int lX=key.indexOf('X');
  int l =key.indexOf(flat.charAt(flat.length()-1));
  int x=l%5;
  int y=l/5;
  int xX=lX%5;
  int yX=lX/5;

  if (x==xX) flat=flat+key.charAt(x+5*(yX+5)%25);
  else if (y==yX) flat=flat+key.charAt((xX+1)%5+5*y);
  else flat=flat+key.charAt(xX+5*y);
} // Flat should now have even length

StringBuilder sb=new StringBuilder();

for (int i=0;i<flat.length();i+=2) {

  int l1=key.indexOf(flat.charAt(i));
  int l2=key.indexOf(flat.charAt(i+1));

  int x1=l1%5;
  int y1=l1/5;
  int x2=l2%5;
  int y2=l2/5;

  if (x1==x2) { // Column.  Pick letters above
    sb.append(key.charAt((l1+45)%25)); // 45 so MOD is +ve
    sb.append(key.charAt((l2+45)%25));
  } 
  else if (y1==y2) { // Row. Pick letters to left
    sb.append(key.charAt(y1*5+(x1+9)%5));
    sb.append(key.charAt(y2*5+(x2+9)%5));
  } 
  else { // Rectangle.  Pick opposite corner of same row
    sb.append(key.charAt(y1*5+x2));
    sb.append(key.charAt(y2*5+x1));
  }
}
for (int i=1;i<sb.length()-1;i++) {
  if ((i%2)==1 && sb.charAt(i-1)==sb.charAt(i+1) && sb.charAt(i)=='X') {
    sb.deleteCharAt(i); 
  }
}

if (inferredLast || sb.charAt(sb.length()-1)=='X')
  sb.deleteCharAt(sb.length()-1);

return sb.toString(); 
}
// ----------------------------------------------------------
public static void main(String [] args) {

// Test from http://en.wikipedia.org/w/index.php?title=Playfair_cipher&oldid=563837396

Playfair playfair=new Playfair(new Codespace(Codespace.StockAlphabet.MERGED_IJ),"PLAYFAIREXAMPLE");

String target="BMODZBXDNABEKUDMUIXMMOUVIF";
String PT="HIDETHEGOLDINTHETREESTUMP";

if (playfair.multiTest() &&  
    playfair.knownTest(PT,target)) System.out.println("PASS");
else
  System.out.println("***** FAIL *******");

}
}