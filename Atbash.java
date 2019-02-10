public class Atbash extends AffineAlphabet
{
// Atbash cipher is ABCD... maps to ZYXW... etc
// Special case of affine with mult=shift=(letters-1)
// See http://en.wikipedia.org/w/index.php?title=Atbash&oldid=557060091

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
Atbash() { super(25,25); }
// ---------------------------------------------------------------------------
Atbash(Codespace cs) 
         { super(cs,cs.PTspace.length()-1,cs.PTspace.length()-1); }
// ---------------------------------------------------------------------------
public static void main(String[] args) {

Atbash atbash=new Atbash();

if (atbash.knownTestEncode("ABCDEF","ZYXWVU")) System.out.println("PASS");
else System.out.println("***** FAIL *************");

}
}