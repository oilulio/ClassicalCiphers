public class Beaufort extends Vigenere
{ 
// A Vigenere variant - see 
// http://en.wikipedia.org/w/index.php?title=Beaufort_cipher&oldid=564005770

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
    
private String sKey;
 
// ----------------------------------------------------------------------
Beaufort(String sKey)  { super(sKey); this.sKey=sKey;  allowable();}  
// ----------------------------------------------------------------------
Beaufort(Codespace cs,String sKey) { super(cs,sKey); allowable();}
// ----------------------------------------------------------------------
void allowable() { // Is this too tight a restriction ?
  if (cs.PTspace.length()%2 !=0) 
    throw new IllegalArgumentException("Beaufort must have even alphabet");
}
// ----------------------------------------------------------------------
private String invertedText(String text)
{
StringBuilder sb=new StringBuilder();
for (int i=0;i<text.length();i++) {
  int index=(cs.PTspace.length()-cs.PTspace.indexOf(text.charAt(i)))%cs.PTspace.length();
  sb.append(cs.PTspace.charAt(index));
}
return sb.toString();
}
// ----------------------------------------------------------------------
@Override
public String toString()
     { return "Beaufort Cipher with key "+sKey+super.toString();}
// ----------------------------------------------------------------------
@Override
public String decode(String text) 
          { return super.encode(invertedText(text)); }
// ----------------------------------------------------------------------
@Override
public String encode(String text) { return decode(text); }  // Beaufort own inverse
// ----------------------------------------------------------------------
public static void main(String [] args)
{
// example from http://practicalcryptography.com/ciphers/beaufort-cipher/
String pt="DEFENDTHEEASTWALLOFTHECASTLE";
String ct="CKMPVCPVWPIWUJOGIUAPVWRIWUUK"; 
Beaufort b=new Beaufort("FORTIFICATION");

if (b.knownTest(pt,ct)) System.out.println("PASS");
else System.out.println("******** FAIL ***********");
}
}