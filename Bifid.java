public class Bifid extends ClassicalCipher
{
// Conducts Bifid encryption and decryption.

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


protected String key;
protected int period;
protected static final int ALL_MESSAGE=0; // Keep as 0

// ----------------------------------------------------------------------
Bifid(String word)     { this(word,ALL_MESSAGE); } 
// ----------------------------------------------------------------------
Bifid(String word,int period) // word can specify all 25 if required
  { this(new Codespace(Codespace.StockAlphabet.MERGED_IJ),word,period); } 
// ----------------------------------------------------------------------
Bifid(Codespace cs,String word)
{
super(cs);
if (cs.PTspace.length() != 25)
  throw new IllegalArgumentException("Bifid needs a 25 letter alphabet");

key=new Keyword().new Simple(cs,word).getKey();
}
// ----------------------------------------------------------------------
Bifid(Codespace cs,String word,int period)
      { this(cs,word); this.period=period; }
// ----------------------------------------------------------------------
@Override
public Bifid copyInstance(ClassicalCipher c)
     { return new Bifid(cs,((Bifid)c).key,((Bifid)c).period); }
// ----------------------------------------------------------------------
public String toString() 
     { return (this.getClass().getName()+" Cipher : Key "+key+" Period = "+
       ((period<=0)?"ALL MESSAGE":period)+nL+super.toString());}
// ----------------------------------------------------------------------
@Override
public String encode(String PT) 
{ 
String flat=cs.flattenToPT(PT);
if (period <= 0) return periodEncode(flat);

StringBuilder sb=new StringBuilder();

for (int i=0;i<((flat.length()-1)/period)+1;i++) {
  int end=((i+1)*period > flat.length())?flat.length():(i+1)*period;
  sb.append(periodEncode(flat.substring(i*period,end)));
}
return sb.toString();
}
// ----------------------------------------------------------------------
private String periodEncode(String PT) 
{
int [][] coords=new int[2][PT.length()];
StringBuilder sb=new StringBuilder();

for (int i=0;i<PT.length();i++) {

  int l1=key.indexOf(PT.charAt(i));
  coords[0][i]=l1/5;
  coords[1][i]=l1%5;
}

for (int i=0;i<PT.length();i++) 
  sb.append(key.charAt(coords[(2*i)/PT.length()][(2*i)%PT.length()]*5+
                       coords[(2*i+1)/PT.length()][(2*i+1)%PT.length()]));
 
return sb.toString(); 
}
// ----------------------------------------------------------------------
@Override
public String decode(String CT) 
{ 
String flat=cs.flattenToCT(CT); 
if (period <= 0) return periodDecode(flat);

StringBuilder sb=new StringBuilder();

for (int i=0;i<((flat.length()-1)/period)+1;i++) {
  int end=((i+1)*period > flat.length())?flat.length():(i+1)*period;
  sb.append(periodDecode(flat.substring(i*period,end)));
}
return sb.toString();
}
// ----------------------------------------------------------------------
private String periodDecode(String CT) 
{
int [][] coords=new int[2][CT.length()];
StringBuilder sb=new StringBuilder();

for (int i=0;i<CT.length();i++) {

  int l1=key.indexOf(CT.charAt(i));
  coords[(2*i)/CT.length()][(2*i)%CT.length()]=l1/5;
  coords[(2*i+1)/CT.length()][(2*i+1)%CT.length()]=l1%5;
}

for (int i=0;i<CT.length();i++) 
  sb.append(key.charAt(coords[0][i]*5+coords[1][i]));
 
return sb.toString(); 
}
// ----------------------------------------------------------
public static void main(String [] args) {

Bifid bifid=new Bifid(new Codespace(Codespace.StockAlphabet.MERGED_IJ),"BGWKZQPNDSIOAXEFCLUMTHYVR");


String target="UAEOLWRINS";
String PT="FLEEATONCE";

if (bifid.multiTest() && bifid.knownTest(PT,target)) System.out.println("PASS");
else  System.out.println("***** FAIL *******");
}
}