public class AutoKeyVigenere extends Vigenere
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
    
String slocalKey; // Need a copy as we make key each time

// ----------------------------------------------------------------------
AutoKeyVigenere(String slocalKey) 
             { super(slocalKey); this.slocalKey=slocalKey;}  
// ----------------------------------------------------------------------
AutoKeyVigenere(Codespace cs,String slocalKey) 
             { super(cs,slocalKey); this.slocalKey=slocalKey; }
// ----------------------------------------------------------------------
@Override
public String encode(String text) 
{
// Dynamically set local key
String flattext=cs.flattenToPT(text);
this.localKey=new int[slocalKey.length()+text.length()];
for (int i=0;i<localKey.length;i++) { // Now use length of array
  char c=(slocalKey+flattext).charAt(i);
  if (cs.PTspace.indexOf(c)<0)  
    throw new RuntimeException(c+" in local key isn't in alphabet "+cs.PTspace);
  this.localKey[i]=cs.PTspace.indexOf(c);
}
// Now our superclass can cope
return super.encode(text);
}
// ----------------------------------------------------------------------
@Override
public String decode(String text) 
{
// Dynamically set local key
String flattext=cs.flattenToCT(text);
this.localKey=new int[slocalKey.length()+text.length()];
for (int i=0;i<localKey.length;i++) { // Now use length of array
  char c=(slocalKey+flattext).charAt(i);
  if (cs.CTspace.indexOf(c)<0)  
    throw new RuntimeException(c+" in local key isn't in alphabet "+cs.CTspace);
  this.localKey[i]=cs.CTspace.indexOf(c);
}
// Now our superclass can cope
return super.decode(text);
}
// ----------------------------------------------------------------------
public static void main(String [] args)
{
String pt="LIGHTSPEEDCHEWIENOW"; // Cryptography w/Java Applets sect 1.10
AutoKeyVigenere akv=new AutoKeyVigenere("ARGH");

// N.B. Encode only ??? TODO doesn't work in reverse?
if (akv.knownTestEncode(pt,"LZMOEAVLXVRLIZKLRKE")) System.out.println("PASS");
else System.out.println("******** FAIL ***********");
}

}