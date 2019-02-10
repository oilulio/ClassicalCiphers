public class Keyword extends Monoalphabetic
{
/* Maker of key word ciphers according to variety of types. 

Keyword cipher is a simple monoalphabetic cipher.  As example,
consider keyword "APE" which expands (using the 'SIMPLE' method) into 
the full monoalphbetic:

ABCDEFGHIJKLMNOPQRSTUVWXYZ mapping to:
APEBCDFGHIJKLMNOQRSTUVWXYZ

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

// ---------------------------------------------------------------------------
Keyword() { this(new Codespace(Codespace.StockAlphabet.CAPITALS)); } // Default
// ---------------------------------------------------------------------------
Keyword(Codespace cs) { super(cs); }
// ----------------------------------------------------------------------
public String getKey() 
{
StringBuilder sb=new StringBuilder(key.length);
for (int i=0;i<key.length;i++)
  sb.append(cs.PTspace.charAt(key[i])); 
return sb.toString();
}
// ---------------------------------------------------------------------------
public void reverse() {

int [] newkey=new int[cs.PTspace.length()];

for (int i=0;i<cs.PTspace.length();i++) 
  newkey[i]=key[cs.PTspace.length()-1-i];

key=newkey;
}
// ----------------------------------------------------------------------
protected boolean makeKey(String mykey) {
// Sets up the int array 

key=new int[cs.PTspace.length()];

if (mykey.length()!=cs.PTspace.length()) {
  System.out.println(mykey);
  System.out.println("ERROR on keylength");
  return false;
}

int sum=0;
for (int i=0;i<cs.PTspace.length();i++) {
  key[i]=cs.PTspace.indexOf(mykey.charAt(i));
  sum+=key[i];
}
if (sum != ((cs.PTspace.length()*(cs.PTspace.length()-1))/2)) { // Make sure use letters just once (not perfect test, but OK)
  System.out.println("ERROR on key characters");
  return false;
}
return true;
}
// ----------------------------------------------------------------------
public class Simple extends Keyword 
{ // Unique letters of word followed by rest of alphabet
Simple(Codespace cs,String word) 
{ 
super(cs);
makeKey(pad(Codespace.unique(cs.flattenToPT(word)))); 
}
}
// ----------------------------------------------------------------------
public class SimpleReverse extends Keyword 
{ // Reversed unique letters of word followed by rest of alphabet
SimpleReverse(Codespace cs,String word) { 
super(cs); 
makeKey(dap(Codespace.unique(cs.flattenToPT(word)))); 
}
}
// ----------------------------------------------------------------------
public class SimpleColumnar extends Keyword 
{ // As SIMPLE but by n columns where n=unique letters
SimpleColumnar(Codespace cs,String word) { 

super(cs);
String solo=Codespace.unique(cs.flattenToPT(word));
int[] order = new int[solo.length()];
for (int i=0;i<solo.length();i++)
order[i]=i; // Simple default
makeKey(byCols(pad(solo),order));  
}
}
// ----------------------------------------------------------------------
public class SimpleColumnarAll extends Keyword 
{ // As SIMPLE_COLUMNAR but n=original letters and omits key letters if repeated
SimpleColumnarAll(Codespace cs,String word) { 

super(cs);
String uword=cs.flattenToPT(word);
int[] order = new int[uword.length()];
for (int i=0;i<uword.length();i++)
  order[i]=i; // Simple default
makeKey(Codespace.unique(byCols(pad(uword),order)));  // No need to invert order - trivial case
}
}
// ----------------------------------------------------------------------
public class SimpleColumnarNulls extends Keyword 
{ // As SIMPLE_COLUMNAR but rest of alphabet has nulls
SimpleColumnarNulls(Codespace cs,String word) { 

super(cs);
String sole=Codespace.unique(cs.flattenToPT(word));
int[] order = new int[sole.length()];
for (int i=0;i<sole.length();i++)
  order[i]=i; // Simple default
makeKey(byCols(padnull(sole),order).replace("_",""));  // No need to invert order - trivial case
}
}
// ----------------------------------------------------------------------
public class Random extends Keyword 
                   { Random(Codespace cs) { super(cs); randomiseKey(); }}
// ----------------------------------------------------------------------

// ----------------------------------------------------------------------
public static void main(String [] args) 
{

}
}