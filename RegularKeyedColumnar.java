public class RegularKeyedColumnar extends KeyedColumnar
{
// Conducts RegularKeyedColumnar encryption and decryption.
// per http://en.wikipedia.org/w/index.php?title=Transposition_cipher&oldid=571502098
// Will pad out to regular case, using defined nulls.

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

String nulls="X";
    
RegularKeyedColumnar(RegularKeyedColumnar from) { super(from); }
// ----------------------------------------------------------------------
RegularKeyedColumnar(String word,String nulls)  { super(word); this.nulls=new String(nulls); }
// ----------------------------------------------------------------------
RegularKeyedColumnar(Codespace cs,String word,String nulls)  { super(cs,word); this.nulls=new String(nulls); }
// ----------------------------------------------------------------------
RegularKeyedColumnar(int [] order,String nulls)              { super(order);  this.nulls=new String(nulls);  }
// ----------------------------------------------------------------------
RegularKeyedColumnar(Codespace cs,int [] order,String nulls) { super(cs,order); this.nulls=new String(nulls); }
// ----------------------------------------------------------------------
@Override
public String encode(String PT) { return super.encode(regularise(PT,key.length,"X")); }
// ----------------------------------------------------------------------
@Override
public String decode(String CT) { return super.decode(regularise(CT,key.length,"X")); }
// Does not obviously make sense to regularise for decode - cannot guarantee
// that nulls would be at end.  However makes RegularKC different from KC -
// i.e. may help some decryptions, and not hinder correct ones where the regular
// length was correct.
// ----------------------------------------------------------------------
public static void main(String [] args)
{
//http://en.wikipedia.org/w/index.php?title=Transposition_cipher&oldid=571502098
String pt="WEAREDISCOVEREDFLEEATONCEQKJEU"; 
RegularKeyedColumnar kc=new RegularKeyedColumnar("ZEBRAS","X");
                           
if (kc.knownTestEncode(pt,"EVLNEACDTKESEAQROFOJDEECUWIREE"))  System.out.println("PASS");
else System.out.println("******** FAIL ***********");

if (kc.cycleTest(pt))  System.out.println("PASS");
else System.out.println("******** FAIL ***********");

// Decode test covered by parent
}
}