import java.util.Date;

public class Monoalphabetic extends ClassicalCipher 
{
// Specifically monoalphabetic ciphers - can have generic encode and decode 

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

protected int[] key;    // The encryption mappings 

// ----------------------------------------------------------------------
Monoalphabetic()  { super(); key=new int[cs.PTspace.length()];}  // Key needs setting later
// ----------------------------------------------------------------------
Monoalphabetic(Codespace cs)   
{ 
super(cs);  
key=new int[cs.PTspace.length()];    // Key needs setting later
}
// ----------------------------------------------------------------------
Monoalphabetic(Codespace cs,String skey)   
{ 
super(cs);  
if (skey.length()!=cs.PTspace.length()) throw new IllegalArgumentException(
    "Key does not cover all of PT space");
key=orderit(skey);
}
// ----------------------------------------------------------------------
Monoalphabetic(Monoalphabetic from)
{
super(from.cs);
key=new int[cs.PTspace.length()];
assignKey(from.key);
}
// --------------------------------------------------------------
@Override
public Monoalphabetic copyInstance(ClassicalCipher c) // Copy factory method
  { return new Monoalphabetic((Monoalphabetic)c); }
 
// --------------------------------------------------------------
protected void assignKey(int [] keytobe) 
{ 
for (int i=0;i<cs.PTspace.length();i++)
  key[i]=keytobe[i];

return;
}
// --------------------------------------------------------------
public boolean equals(Monoalphabetic ma) 
{
  if (!cs.PTspace.equals(ma.cs.PTspace)) return false;
  if (!cs.PTmap.equals(ma.cs.PTmap))   return false;

  for (int i=0;i<key.length;i++)
    if (key[i]!=ma.key[i]) return false;

return true;
}
// --------------------------------------------------------------
public String toString() 
     { return (this.getClass().getName()+" Cipher : Key "+showKey()+
               super.toString());}
// --------------------------------------------------------------
public String showKey() { return encode(cs.PTspace); } 
// --------------------------------------------------------------
public void randomiseKey() // Sets a fully random key
{
key=new int[cs.PTspace.length()];
boolean [] used=new boolean[key.length];

for (int i=0;i<key.length;i++) {       
  key[i]=rand.nextInt(key.length);
  while (used[key[i]])    key[i]=rand.nextInt(key.length);
  used[key[i]]=true;
}
return;
}
// --------------------------------------------------------------
public void randomiseMirrorKey() // Forces a mirrored key i.e. A<->B etc
// If odd number of chars, has one instance of a <-> a
{
int same;
key=new int[cs.PTspace.length()];
boolean [] used=new boolean[key.length];

if ((key.length % 2)==1) {
  same=rand.nextInt(key.length);
  used[same]=true;
  key[same]=same;
}

int nkeys=key.length/2;  // designed to round and lose odd

int index=rand.nextInt(key.length);
for (int i=0;i<nkeys;i++) {       
  while (used[index]) index=rand.nextInt(key.length);
  used[index]=true;
  key[index]=rand.nextInt(key.length);
  while (used[key[index]])    key[index]=rand.nextInt(key.length);
  used[key[index]]=true;
  key[key[index]]=index;  // Recripocal
  
  index=rand.nextInt(key.length);
}
return;
}
// ----------------------------------------------------------------------
@Override
public String encode(String PT) 
{
String flat=cs.flattenToPT(PT);

StringBuilder sb=new StringBuilder();
for (int i=0;i<flat.length();i++)
  sb.append(cs.CTspace.charAt(key[cs.PTspace.indexOf(flat.charAt(i))]));
return sb.toString();
}
// ----------------------------------------------------------------------
@Override
public String decode(String CT) 
{
String flat=cs.flattenToCT(CT);
StringBuilder sb=new StringBuilder();
int[] invkey=invert(key); // Only used here, so set before use
for (int i=0;i<flat.length();i++)
  sb.append(cs.PTspace.charAt(invkey[cs.CTspace.indexOf(flat.charAt(i))]));
return sb.toString();
}
// ----------------------------------------------------------------------
public static void main(String [] args) {

Monoalphabetic mo=new Monoalphabetic(new Codespace(Codespace.StockAlphabet.CAPITALS));

}
}