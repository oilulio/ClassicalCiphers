public class AffineAlphabet extends Monoalphabetic
{
// Conducts Affine Transformation encryption and decryption.
// by shifting the alphabet (alternative is transposition of written letter order)

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

int mult,shift;
// ---------------------------------------------------------------------------
AffineAlphabet(int mult,int shift)
{
super();
setKey(mult,shift);
}
// ---------------------------------------------------------------------------
AffineAlphabet(Codespace cs,int mult,int shift)
{  
super(cs);  
setKey(mult,shift);
}
// ---------------------------------------------------------------------------
protected void setKey(int mult,int shift)
{
this.mult=mult;
this.shift=shift;
key=new int[cs.PTspace.length()];
for (int i=0;i<cs.PTspace.length();i++)
  key[i]=(i*mult+shift)%cs.PTspace.length();
}
// --------------------------------------------------------------
public String toString() 
     { return (this.getClass().getName()+" Cipher : Multiplier= "+mult+
               " Shift= "+shift+nL+super.toString());}
// ----------------------------------------------------------------------
public static void main(String [] args)
{
// http://en.wikipedia.org/w/index.php?title=Affine_cipher&oldid=540372488
AffineAlphabet a=new AffineAlphabet(5,8);
if (a.knownTest("AFFINECIPHER","IHHWVCSWFRCP")) System.out.println("PASS");
else System.out.println("******** FAIL ***********");
}
}