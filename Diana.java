public class Diana extends ClassicalCipher
{
// A Vietnam era adjunct to OTP.  Produces a Vigenere-like tableau
    
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
  
protected String OTPKey;

// ----------------------------------------------------------------------
Diana(String OTPKey)  
   { this(new Codespace(Codespace.StockAlphabet.CAPITALS),OTPKey); }  
// ----------------------------------------------------------------------
Diana(Codespace cs,String OTPKey) 
         { super(cs); setKey(OTPKey); } 
// ----------------------------------------------------------------------
protected void setKey(String OTPKey) 
{
  this.OTPKey=OTPKey;
  for (int i=0;i<OTPKey.length();i++) {
    char c=OTPKey.charAt(i);
    if (cs.PTspace.indexOf(c)<0)  
      throw new RuntimeException(c+" in local key isn't in alphabet "+cs.PTspace);
  }
}
// ----------------------------------------------------------------------
@Override 
public String encode(String text) 
{
if (text.length()>OTPKey.length()) throw new IllegalArgumentException(
  "Text cannot be longer than OTP");
  
String flattext=cs.flattenToPT(text);
StringBuilder sb=new StringBuilder(text.length());

for (int i=0;i<text.length();i++) {
  int a=cs.PTspace.indexOf(OTPKey.charAt(i));
  int b=cs.PTspace.indexOf(text.charAt(i));
  int c=(cs.PTspace.length()*2-1-a-b)%cs.PTspace.length();
  sb.append(cs.PTspace.charAt(c));
}
return sb.toString();
}
// ----------------------------------------------------------------------
@Override
public String decode(String text) { return encode(text); }  // Reciprocal
// ----------------------------------------------------------------------
public static void main(String [] args)
{
String PT="ATTACKATDAWN";    // Example from https://programmingpraxis.com/2014/12/19/diana-cryptosystem/
String CT="TSPDZTVNRIBY";

Diana diana=new Diana("GORWYWETFRCOYET");

if (diana.knownTest(PT,CT)) System.out.println("PASS");
else System.out.println("******** FAIL ***********");
}
}