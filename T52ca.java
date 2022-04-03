public class T52ca extends T52c
{
/*
Copyright (C) 2020  S Combes

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. 
*/

// Only difference from T52c is the SR mapping. 
static final int [] SRmaskCA={0x291,0x151,0x268,0x00F,0x138,0x286,0x0AC,0x116,0x1E0,0x243};
// SR05..01..SR10..SR06 (algorithm <<=1 reverses order)
// ----------------------------------------------------------------------
T52ca(String wheelSet,String wheelPos,String MKUkey) { super(wheelSet,wheelPos,MKUkey); SRmask=SRmaskCA;}
// ----------------------------------------------------------
public static void main(String [] args) {

// Comparison with MTC3 JAR File
String PT="OUR9DOUBTS9ARE9TRAITORS9AND9MAKE9US9LOSE9THE9GOOD9WE9OFT9MIGHT9WIN9BY9FEARING9TO9ATTEMPT+QWE8";
String CT="3LJSKLYZ9QRNFUULHWZAWWNVDBLLQDWOGZIKAYGURJCAKZ8CIFPTRZCG+GSIVYQEFPAGOZKWNZWWCZPEM49E4NTEI3LQV";

T52ca t52ca=new T52ca("I:9:II:7:III:5:IV:3:V:1","01:02:03:04:05:06:07:08:09:10","TXYSP");

boolean pass=t52ca.knownTestEncode(PT,CT);
t52ca=new T52ca("I:9:II:7:III:5:IV:3:V:1","01:02:03:04:05:06:07:08:09:10","TXYSP");
pass&=t52ca.knownTestDecode(PT,CT);

if (pass) System.out.println("PASS");
else System.out.println("******** FAIL ***********");
}
}
