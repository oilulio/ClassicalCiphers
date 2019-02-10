public class Gronsfeld extends Vigenere
{ 
// Equivalent to Vigenere with key defined as 0-9 not A-Z

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
Gronsfeld(String slocalKey)  { super(alphabetiseKey(slocalKey)); }  
// ----------------------------------------------------------------------
Gronsfeld(Codespace cs,String slocalKey) 
          { super(cs,alphabetiseKey(slocalKey)); }
// ----------------------------------------------------------------------
protected static String alphabetiseKey(String slocalKey) 
{
StringBuilder sb=new StringBuilder("");
for (int i=0;i<slocalKey.length();i++) {
  if (Character.isDigit(slocalKey.charAt(i))) {
    sb.append((char)((int)slocalKey.charAt(i)-48+65));
  }
  else
    throw new IllegalArgumentException(
             "Gronsfield key isn't digit "+slocalKey);
}
return sb.toString();
}
// ----------------------------------------------------------------------
public static void main(String [] args)
{
String pt="COMETOTHERESCUE"; // Cryptography w/Java Applets sect 9.7
Gronsfeld g=new Gronsfeld("31485");
if (g.knownTest(pt,"FPQMYRULMWHTGCJ")) System.out.println("PASS");
else System.out.println("******** FAIL ***********");
}
}