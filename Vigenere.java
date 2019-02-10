public class Vigenere extends TrueVigenere
{ 
// Subset of True Vigenere with no keyword - hence fake with
// a 'keyword' of the first letter in our alphabet = constant

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
Vigenere(String slocalKey)  
  { super(new Codespace(Codespace.StockAlphabet.CAPITALS),"A",slocalKey); }  
// ----------------------------------------------------------------------
Vigenere(Codespace cs,String slocalKey) 
         { super(cs,cs.PTmap.substring(0,1),slocalKey); }
// ----------------------------------------------------------------------
public static void main(String [] args)
{
String pt="DANGERWILLROBINSON"; // Cryptography w/Java Applets sect 5.5
Vigenere v=new Vigenere("SPACE");
if (v.knownTest(pt,"VPNIIJLINPJDBKRKDN")) System.out.println("PASS");
else System.out.println("******** FAIL ***********");
}
}