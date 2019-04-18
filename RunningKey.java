public class RunningKey extends TrueVigenere
{ 
// Subset of True Vigenere with no keyword - hence fake with
// a 'keyword' of the first letter in our alphabet = constant

// N.B. Identical to Vigenere.java - but use non-repeating key

// Note ability to use a non-alphabetic tableau if a different
// Codespace is used (e.g. Z-A rather than A-Z)

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
RunningKey(String slocalKey)  
  { super(new Codespace(Codespace.StockAlphabet.CAPITALS),"A",slocalKey); }  
// ----------------------------------------------------------------------
RunningKey(Codespace cs,String slocalKey) 
         { super(cs,cs.PTmap.substring(0,1),slocalKey); }
// ----------------------------------------------------------------------
public static void main(String [] args)
{
String pt="FLEEATONCEWEAREDISCOVERED"; // https://en.wikipedia.org/w/index.php?title=Running_key_cipher&oldid=883541446

RunningKey rk=new RunningKey("ERRORSCANOCCURINSEVERALPL");
if (rk.knownTest(pt,"JCVSRLQNPSYGUIMQAWXSMECTO")) System.out.println("PASS");
else System.out.println("******** FAIL ***********");
}
}

