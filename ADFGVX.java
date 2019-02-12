public class ADFGVX extends Digraph
{
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
  
// see https://en.wikipedia.org/w/index.php?title=ADFGVX_cipher&oldid=869890090
protected String key;
RegularKeyedColumnar kc;

// ----------------------------------------------------------------------
ADFGVX(String word1,String word2)  
{
super(new Codespace(Codespace.ALPHABET+Codespace.NUMBERS,
                    Codespace.ALPHABET+Codespace.NUMBERS,"ADFGVX","ADFGVX"),6,word1);
kc=new RegularKeyedColumnar(cs,word2,"X");
}
// ----------------------------------------------------------------------
public String toString() 
     { return (this.getClass().getName()+" Cipher : Key "+key+
               nL+"Columnar key "+kc.key+nL+super.toString());}
// ----------------------------------------------------------
@Override
public String encode(String PT)  { return kc.encode(super.encode(PT)); }
// ----------------------------------------------------------------------
@Override
public String decode(String CT)  { return super.decode(kc.decode(CT)); }
// ----------------------------------------------------------
public static void main(String [] args) {

// Test from https://en.wikipedia.org/w/index.php?title=ADFGVX_cipher&oldid=869890090

ADFGVX adfgvx=new ADFGVX("NA1C3H8TB2OME5WRPD4F6G7I9J0KLQSUVXYZ","PRIVACY");

if (adfgvx.knownTest("ATTACKAT1200AM","DGDDDAGDDGAFADDFDADVDVFAADVX")) System.out.println("PASS");
else System.out.println("******** FAIL ***********");

}
}