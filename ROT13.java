public class ROT13 extends AffineAlphabet
{
// Caesar cipher is ABCD... maps to NOPQ... etc
// Special case of affine with mult=1, shift=13

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
ROT13() { super(1,13); }
// ---------------------------------------------------------------------------
// Doesn't really make sense for other alphabets, although see ROT47

public static void main(String[] args) {

ROT13 r13=new ROT13();
if (r13.knownTest("ABCD","NOPQ")) System.out.println("PASS");
else System.out.println("******** FAIL ***********");
}
}