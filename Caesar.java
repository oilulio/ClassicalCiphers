public class Caesar extends AffineAlphabet
{
// Caesar cipher is ABCD... maps to DEFG... etc
// Special case of affine with mult=1, shift=3
// But allow other possible Caesars with specified shift

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
Caesar()                        { super(1,3); }
// ---------------------------------------------------------------------------
Caesar(int shift)               { super(1,shift); }
// ---------------------------------------------------------------------------
Caesar(Codespace cs,int shift)  { super(cs,1,shift); }
// ---------------------------------------------------------------------------
Caesar(Codespace cs)            { super(cs,1,3); }
// ---------------------------------------------------------------------------
public static void main(String[] args) {

Caesar caesar=new Caesar();
if (caesar.knownTest("ABCDEF","DEFGHI")) System.out.println("PASS");
else System.out.println("********* FAIL ***********");

}
}