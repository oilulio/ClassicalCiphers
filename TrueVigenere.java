public class TrueVigenere extends ClassicalCipher
{
// The most general form ofthe Vigenere cipher.  Superclass for simpler 
// variants (e.g Vigenere, Beaufort)
// Specifcially there is a key for both the row elements within the table
// and the order in which the rows are selected.
  
// TrueVigenere used for Kryptos K1 and K2  
  
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
  
protected int[] mainKey;    
protected int[] invMainKey;    // inverted
protected int[] localKey;     

// ----------------------------------------------------------------------
TrueVigenere(String smainKey,String slocalKey)  
   { this(new Codespace(Codespace.StockAlphabet.CAPITALS),smainKey,slocalKey); }  
// ----------------------------------------------------------------------
TrueVigenere(Codespace cs,String smainKey,String slocalKey) 
         { super(cs); setKey(smainKey,slocalKey); } 
// ----------------------------------------------------------------------
protected void setKey(String smainKey,String slocalKey) 
{
  this.mainKey=orderit(pad(smainKey)); 
  invMainKey=invert(this.mainKey);
  this.localKey=new int[slocalKey.length()];
  for (int i=0;i<slocalKey.length();i++) {
    char c=slocalKey.charAt(i);
    if (cs.PTspace.indexOf(c)<0)  
      throw new RuntimeException(c+" in local key isn't in alphabet "+cs.PTspace);
    this.localKey[i]=cs.PTspace.indexOf(c);
  }
}
// ----------------------------------------------------------------------
@Override 
public String encode(String text) 
{
String flattext=cs.flattenToPT(text);
StringBuilder sb=new StringBuilder(text.length());

for (int i=0;i<text.length();i++) {
  int shift = invMainKey[localKey[i%localKey.length]];
  sb.append(cs.PTspace.charAt(mainKey[
     (invMainKey[cs.PTspace.indexOf(text.charAt(i))]+shift)%cs.PTspace.length()]));
}
return sb.toString();
}
// ----------------------------------------------------------------------
@Override
public String decode(String text) 
{
String flat=cs.flattenToCT(text);
StringBuilder sb=new StringBuilder(text.length());

for (int i=0;i<flat.length();i++) {
  int shift = invMainKey[localKey[i%localKey.length]]-cs.PTspace.length();
  // Subtraction was to keep MOD +ve
  sb.append(cs.PTspace.charAt(mainKey[
        (invMainKey[cs.PTspace.indexOf(flat.charAt(i))]-shift)%cs.PTspace.length()]));
}
return sb.toString();
}
// ----------------------------------------------------------------------
public static void main(String [] args)
{
String k1PT="BETWEENSUBTLESHADINGANDTHEABSENCEOFLIGHTLIESTHENUANCEOFIQLUSION";
String k1CT="EMUFPHZLRFAXYUSDJKZLDKRNSHGNFIVJYQTQUXQBQVYUVLLTREVJYQTMKYRDMFD";
TrueVigenere k1=new TrueVigenere("KRYPTOS","PALIMPSEST");

if (k1.knownTest(k1PT,k1CT)) System.out.println("PASS");
else System.out.println("******** FAIL ***********");
}
}