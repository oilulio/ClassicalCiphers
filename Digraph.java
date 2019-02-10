public class Digraph extends ClassicalCipher
{  // Base for digraphs like ADFGX. 
   // Sets 'sizex' the row length of the Digraph table, e.g.

//  5         7
// ABCDE    ABCDEFGH
// EFGIK    IJKLMNOP
// LMNOP    QRSTUVWX
// QRSTU    YZ
// VWXYZ

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
protected int sizex;

// ----------------------------------------------------------------------
// No default codespace, because standard alphabet is nota  square
// ----------------------------------------------------------------------
Digraph(Codespace cs,int sizex,String word)  
{
super(cs);
key=new Keyword().new Simple(cs,word).getKey(); 
this.sizex=sizex;
}
// ----------------------------------------------------------------------
public String toString() 
     { return (this.getClass().getName()+" Cipher : Key "+key+
               nL+super.toString());}
// ----------------------------------------------------------
@Override
public String encode(String PT) 
{ 
String flat=cs.flattenToPT(PT);
StringBuilder sb=new StringBuilder(flat.length()*2);

for (int i=0;i<flat.length();i++) {
  int c=key.indexOf(flat.charAt(i));
  sb.append(cs.CTspace.charAt(c/sizex));
  sb.append(cs.CTspace.charAt(c%sizex));
}
return sb.toString();
}
// ----------------------------------------------------------------------
@Override
public String decode(String CT) 
{ 
String flat=cs.flattenToCT(CT); 
StringBuilder sb=new StringBuilder(CT.length()/2);

for (int i=0;i<flat.length()-1;i+=2) 
  sb.append(key.charAt(sizex*cs.CTspace.indexOf(flat.charAt(i))+
                             cs.CTspace.indexOf(flat.charAt(i+1))));

return sb.toString();
}
}