public class ADFGX extends Digraph
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
  
// see http://en.wikipedia.org/w/index.php?title=ADFGVX_cipher&oldid=565756009
protected String key;
RegularKeyedColumnar kc;

// ----------------------------------------------------------------------
ADFGX(String word1,String word2)  
{
super(new Codespace(Codespace.ALPHABET,Codespace.IJmerge,"ADFGX","ADFGX"),5,word1);
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

// Test from http://en.wikipedia.org/w/index.php?title=ADFGVX_cipher&oldid=565756009

ADFGX adfgx=new ADFGX("BTALPDHOZKQFVSNGICUXMREWY","CARGO");

if (adfgx.knownTest("ATTACKATONCE","FAXDFADDDGDGFFFAFAXXAFAFX")) System.out.println("PASS");
else System.out.println("******** FAIL ***********");

}
}