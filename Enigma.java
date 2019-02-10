import java.util.ArrayList;
import java.util.List;

// Implements Enigma machine for a wide variety of cases/variants
// including the 4 wheel Kreigsmarine machine

public class Enigma extends ClassicalCipher
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
    
public  static enum Wheel { NONE,
         CommercialAB_IC,CommercialAB_IIC,CommercialAB_IIIC,
         Rocket_I,Rocket_II,Rocket_III,
         Swiss_KI,Swiss_KII,Swiss_KIII,
         Enigma_I,Enigma_II,Enigma_III,Enigma_IV,Enigma_V,Enigma_VI,
         Enigma_VII,Enigma_VIII, Beta, Gamma };

public static enum Reflect { NONE, Rocket, Swiss_K, A, B, C, B_Thin, C_Thin, Typex};

public static enum ETW { NONE, Rocket, Swiss_K };

public static String NO_STECKER = "";

Rotor rotor0;   // The extra 4th rotor - Kreigsmarine - very left hand
Rotor rotorA;   // Third from right
Rotor rotorB;   // Second from right
Rotor rotorC;   // Right hand

Plugs            plugs;     // aka Steckers
Eintrittswalze   etw;       // Input mapping   
Reflector        reflector; // aka UKW

// This constructor solely used by Typex class.
protected Enigma(Wheel w1,Wheel w2,Wheel w3,Wheel s1,Wheel s2,String initial,String ring)
{
this(Wheel.NONE,w1,w2,w3,Reflect.Typex,"","A"+initial.substring(0,3),"A"+ring.substring(0,3)); 
// Fake as a 4-wheel with blank steckers, then construct special monoalphabetic for steckers.
} 

Enigma(Wheel w1,Wheel w2,Wheel w3,Reflect r,String stecker,String initial,String ring)
    { this(Wheel.NONE,w1,w2,w3,r,stecker,"A"+initial,"A"+ring); } // Fake a 4-wheel

Enigma(Wheel w1,Wheel w2,Wheel w3,Wheel w4,Reflect r,String stecker,String initial,String ring)
{
super(new Codespace(Codespace.StockAlphabet.CAPITALS));

if (r==Reflect.Rocket) {
  ring=ring.substring(1); // Strip the dummy we added
  initial=initial.substring(1); // ditto
  if (ring.length()!=4) throw new IllegalArgumentException("Rocket should have had "+
    "4 ring settings, reflector & three wheels");
  reflector=new Reflector(r,initial.substring(0,1),ring.substring(0,1));
  etw=new Eintrittswalze(ETW.Rocket);
}
else {
  reflector=new Reflector(r);
  etw=new Eintrittswalze(ETW.NONE);
}
rotor0=new Rotor(w1,initial.substring(0,1),ring.substring(0,1),!reflector.noReflector());
rotorA=new Rotor(w2,initial.substring(1,2),ring.substring(1,2),!reflector.noReflector());
rotorB=new Rotor(w3,initial.substring(2,3),ring.substring(2,3),!reflector.noReflector());
rotorC=new Rotor(w4,initial.substring(3,4),ring.substring(3,4),!reflector.noReflector());

plugs=new Plugs(cs,stecker); 
}
// ---------------------------------------------------------------------
public void updateStecker(String stecker) {  plugs=new Plugs(cs,stecker); }
// ---------------------------------------------------------------------
@Override
public String toString()
{
StringBuilder sb=new StringBuilder();

sb.append("Enigma Machine with "+
    ((reflector.reflect!=Reflect.NONE)?"reflector and ":"")+
    ((rotor0.wheel==Wheel.NONE)?3:4)+" wheels"+nL+
    "Wheels from Left to Right are :"+nL);

sb.append(reflector.toString());
if (rotor0.wheel!=Wheel.NONE) {
  sb.append(rotor0.wheel+" "+rotor0.letters+" Rollover "+(char)(rotor0.roll1+65));
  sb.append(" "+((rotor0.roll1!=rotor0.roll2)?((char)(rotor0.roll1+65)+nL):nL));
}

sb.append(rotorA.wheel+" "+rotorA.letters+" Rollover "+(char)(rotorA.roll1+65));
sb.append(" "+((rotorA.roll1!=rotorA.roll2)?((char)(rotorA.roll1+65)+nL):nL));

sb.append(rotorB.wheel+" "+rotorB.letters+" Rollover "+(char)(rotorB.roll1+65));
sb.append(" "+((rotorB.roll1!=rotorB.roll2)?((char)(rotorB.roll1+65)+nL):nL));

sb.append(rotorC.wheel+" "+rotorC.letters+" Rollover "+(char)(rotorC.roll1+65));
sb.append(" "+((rotorC.roll1!=rotorC.roll2)?((char)(rotorC.roll1+65)+nL):nL));

sb.append(plugs.toString());
sb.append(etw.toString());

int k0=(rotor0.position%26);
int kA=(rotorA.position%26);
int kB=(rotorB.position%26);
int kC=(rotorC.position%26);

sb.append("Key setting  : "+((rotor0.wheel==Wheel.NONE)?"":cs.PTspace.substring(k0,k0+1))+
   cs.PTspace.substring(kA,kA+1)+cs.PTspace.substring(kB,kB+1)+cs.PTspace.substring(kC,kC+1)+nL);

int r0=((26-rotor0.ring)%26);
int rA=((26-rotorA.ring)%26);
int rB=((26-rotorB.ring)%26);
int rC=((26-rotorC.ring)%26);

sb.append("Ring setting : "+((rotor0.wheel==Wheel.NONE)?"":cs.PTspace.substring(r0,r0+1))+
   cs.PTspace.substring(rA,rA+1)+cs.PTspace.substring(rB,rB+1)+cs.PTspace.substring(rC,rC+1)+nL);

return sb.toString();
}
// ---------------------------------------------------------------------
@Override
public String decode(String CT)  { return encode(CT); } // Encode=Decode 
// ---------------------------------------------------------------------
@Override
public String encode(String PT)
{
StringBuilder sb=new StringBuilder();

String steckered=etw.decode(plugs.encode(PT.replace(" ","")));

for (int i=0;i<steckered.length();i++) {

// see http://www.cryptomuseum.com/crypto/enigma/working.htm
// Some machined such as the Zahlwerksmaschine A28 and the Enigma G, 
// were driven by a gear mechanism with cog-wheels rather than by 
// pawls and rachets. These machines do not suffer from the double
// stepping anomaly and behave exactly like the odometer of a car. 

/*  if (rotorC.atRollover()) { // Odometer mode - not implemented 
    if (rotorB.atRollover())
      rotorA.roll();
    rotorB.roll();
  }
  rotorC.roll(); */

// See http://practicalcryptography.com/ciphers/enigma-cipher/
  if (rotorB.atRollover()) {
    rotorA.roll();
    rotorB.roll();  // B triggers A
//    System.out.println("A and B roll @ character "+(i+1));
  }
  else if (rotorC.atRollover()) { // Or B can go on its own
    rotorB.roll();
//    System.out.println("B rolls @ character "+(i+1));
  }
  rotorC.roll();  // C always moves

// Rotor 0 (4th) never moves - sect 6.2 of www.xat.nl/enigma-e

// some models, the reflector moves (Not Enigma I)

  sb.append((char)(  // Left through rotors C->B->A->0; Reflect; then 0->A->B->C
            rotorC.permR(
            rotorB.permR(
            rotorA.permR(
            rotor0.permR(reflector.encode(
            rotor0.permL(
            rotorA.permL(
            rotorB.permL(
            rotorC.permL((int)steckered.charAt(i)-65)))))))))%26+65)); 

}
return etw.encode(plugs.decode(sb.toString()));  
}
// ----------------------------------------------------------------------
@Override
public void reset() 
{  rotor0.reset(); rotorA.reset(); rotorB.reset(); rotorC.reset();  }
// ----------------------------------------------------------------------
@Override
public boolean knownTest(String PT,String CT) // Tolerate spaces
{  return super.knownTest(PT.replace(" ",""),CT.replace(" ",""));  }
// ---------------------------------------------------------------------
public static void main(String [] args) {

// Test mchine against various known solutions

Enigma enigma;
String CT="DUMMY";
boolean passed=true;

// Instruction manual ------------------------------------------------------------------
enigma=new Enigma(Wheel.Enigma_II,Wheel.Enigma_I,Wheel.Enigma_III,
                         Reflect.A,"AM FI NV PS TU WZ","ABL","XMV");

//System.out.println("......................... Instruction Manual Test "+nL+enigma);

passed&=enigma.knownTest("FEIND LIQEI NFANT ERIEK OLONN EBEOB AQTET XANFA NGSUE "+
                         "DAUSG ANGBA ERWAL DEXEN DEDRE IKMOS TWAER TSNEU STADT",
                         "GCDSE AHUGW TQGRK VLFGX UCALX VYMIG MMNMF DXTGN VHVRM "+
                         "MEVOU YFZSL RHDRR XFJWC FHUHM UNZEF RDISI KBGPM YVXUZ");

// Turing's Treatise ------------------------------------------------------------------

enigma=new Enigma(Wheel.Rocket_III,Wheel.Rocket_I,Wheel.Rocket_II,
                         Reflect.Rocket,NO_STECKER,"JEZA","ZQPM");

//System.out.println("......................... Turing Treatise Test "+nL+enigma);

passed&=enigma.knownTest("DEUTS QETRU PPENS INDJE TZTIN ENGLA ND",
                         "QSZVI DVMPN EXACM RWWXU IYOTY NGVVX DZ");

// Op BARBAROSSA Test 1 ------------------------------------------------------------------
enigma=new Enigma(Wheel.Enigma_II,Wheel.Enigma_IV,Wheel.Enigma_V,
                         Reflect.B,"AV BS CG DL FU HZ IN KM OW RX","BLA","BUL");

//System.out.println("......................... Barbarossa I Test "+nL+enigma);

passed&=enigma.knownTest(
                "AUFKLXABTEILUNGXVONXKURTINOWAXKURTINOWAXNORDWESTLXSEBEZXSEBEZXUAFFLI"+
                "EGERSTRASZERIQTUNGXDUBROWKIXDUBROWKIXOPOTSCHKAXOPOTSCHKAXUMXEINSAQTD"+
                "REINULLXUHRANGETRETENXANGRIFFXINFXRGTX",
                "EDPUD NRGYS ZRCXN UYTPO MRMBO FKTBZ REZKM LXLVE FGUEY SIOZV EQMIK "+
                "UBPMM YLKLT TDEIS MDICA GYKUA CTCDO MOHWX MUUIA UBSTS LRNBZ SZWNR "+
                "FXWFY SSXJZ VIJHI DISHP RKLKA YUPAD TXQSP INQMA TLPIF SVKDA SCTAC DPBOP VHJK");


// Op BARBAROSSA Test 2 ---------------------------------------------------------------------
enigma=new Enigma(Wheel.Enigma_II,Wheel.Enigma_IV,Wheel.Enigma_V,
                         Reflect.B,"AV BS CG DL FU HZ IN KM OW RX","LSD","BUL");

//System.out.println("......................... Barbarossa II Test "+nL+enigma);

passed&=enigma.knownTest("DREIG EHTLA NGSAM ABERS IQERV ORWAE RTSXE INSSI EBENN ULLSE QSXUH "+
                         "RXROE MXEIN SXINF RGTXD REIXA UFFLI EGERS TRASZ EMITA NFANG XEINS "+
                         "SEQSX KMXKM XOSTW XKAME NECXK",
                         "SFBWD NJUSE GQOBH KRTAR EEZMW KPPRB XOHDR OEQGB BGTQV PGVKB VVGBI "+
                         "MHUSZ YDAJQ IROAX SSSNR EHYGG RPISE ZBOVM QIEMM ZCYSG QDGRE RVBIL "+
                         "EKXYQ IRGIR QNRDN VRXCY YTNJR");

// U-264 ------------------------------------------------------------------
enigma=new Enigma(Wheel.Beta,Wheel.Enigma_II,Wheel.Enigma_IV,Wheel.Enigma_I,
                         Reflect.B_Thin,"AT BL DF GJ HM NW OP QY RZ VX","VJNA","AAAV");

//System.out.println("......................... U-264 Test "+nL+enigma);

passed&=enigma.knownTest("NCZW VUSX PNYM INHZ XMQX SFWX WLKJ AHSH NMCO CCAK UQPM KCSM HKSE "+
         "INJU SBLK IOSX CKUB HMLL XCSJ USRR DVKO HULX WCCB GVLI YXEO AHXR HKKF VDRE WEZL XOBA "+
         "FGYU JQUK GRTV UKAM EURB VEKS UHHV OYHA BCJW MAKL FKLM YFVN RIZR VVRT KOFD ANJM OLBG "+
         "FFLE OPRG TFLV RHOW OPBE KVWM UQFM PWPA RMFH AGKX IIBG",
         "VONV ONJL OOKS JHFF TTTE INSE INSD REIZ WOYY QNNS NEUN INHA LTXX BEIA NGRI FFUN TERW "+
         "ASSE RGED RUEC KTYW ABOS XLET ZTER GEGN ERST ANDN ULAC HTDR EINU LUHR MARQ UANT ONJO "+
         "TANE UNAC HTSE YHSD REIY ZWOZ WONU LGRA DYAC HTSM YSTO SSEN ACHX EKNS VIER MBFA ELLT "+
         "YNNN NNNO OOVI ERYS ICHT EINS NULL ");

// Scarnhorst ------------------------------------------------------------------
enigma=new Enigma(Wheel.Enigma_III,Wheel.Enigma_VI,Wheel.Enigma_VIII,
                         Reflect.B,"AN EZ HK IJ LR MQ OT PV SW UX","UZV","AHM");

//System.out.println("......................... Scharnhorst Test "+nL+enigma);

passed&=enigma.knownTest("STEUE REJTA NAFJO RDJAN STAND ORTQU AAACC CVIER NEUNN "+
                         "EUNZW OFAHR TZWON ULSMX XSCHA RNHOR STHCO ",
                         "YKAE NZAP MSCH ZBFO CUVM RMDP YCOF HADZ IZME FXTH FLOL "+
                         "PZLF GGBO TGOX GRET DWTJ IQHL MXVJ WKZU ASTR");

// Domnitz ------------------------------------------------------------------
enigma=new Enigma(Wheel.Beta,Wheel.Enigma_V,Wheel.Enigma_VI,Wheel.Enigma_VIII,
                         Reflect.C_Thin,"AE BF CM DQ HU JN LX PR SZ VW ","CDSZ","EPEL");
// Use thin C - book says 'C'
//System.out.println("......................... Donitz Test "+nL+enigma);

passed&=enigma.knownTest("KRKRALLEXXFOLGENDESISTSOFORTBEKANNTZUGEBENXXICHHABEFOLGELNBEBEFEHLERH"+
   "ALTENXXJANSTERLEDESBISHERIGXNREICHSMARSCHALLSJGOERINGJSETZTDERFUEHRER"+
   "SIEYHVRRGRZSSADMIRALYALSSEINENNACHFOLGEREINXSCHRIFTLSCHEVOLLMACHTUNTE"+
   "RWEGSXABSOFORTSOLLENSIESAEMTLICHEMASSNAHMENVERFUEGENYDIESICHAUSDERGEG"+
   "ENWAERTIGENLAGEERGEBENXGEZXREICHSLEITEIKKTULPEKKJBORMANNJXXOBXDXMMMDU"+
   "RNHFKSTXKOMXADMXUUUBOOIEXKP ","LANO TCTO UARB BFPM HPHG CZXT DYGA HGUF XGEW KBLK GJWL QXXT"+
   "GPJJ AVTO CKZF SLPP QIHZ FXOE BWII EKFZ LCLO AQJU LJOY HSSM BBGW HZAN"+
   "VOII PYRB RTDJ QDJJ OQKC XWDN BBTY VXLY TAPG VEAT XSON PNYN QFUD BBHH"+
   "VWEP YEYD OHNL XKZD NWRH DUWU JUMW WVII WZXI VIUQ DRHY MNCY EFUA PNHO"+
   "TKHK GDNP SAKN UAGH JZSM JBMH VTRE QEDG XHLZ WIFU SKDQ VELN MIMI THBH"+
   "DBWV HDFY HJOQ IHOR TDJD BWXE MEAY XGYQ XOHF DMYU XXNO JAZR SGHP LWML"+
   "RECW WUTL RTTV LBHY OORG LGOW UXNX HMHY FAAC QEKT HSJW ");

if (passed) System.out.println("PASS");
else        System.out.println("********* FAIL **********");
}
// ---------------------------------------------------------------------
private class Plugs extends Keyword {
// Monoalphabetic plugboard cipher.  Keyword of form AB CQ XY where A<->B,
// C<->Q etc.  Spaces tolerated.  Unspecified letters map to selves.
Plugs(Codespace cs,String plugs) 
{
super(cs);
String uword=cs.flattenToPT(plugs.replace(" ",""));
key=new int[cs.PTspace.length()];
for (int i=0;i<cs.PTspace.length();i++)
  key[i]=i;

for (int i=0;i<uword.length();i+=2) {
  key[cs.PTspace.indexOf(uword.charAt(i))]=cs.PTspace.indexOf(uword.charAt(i+1));
  key[cs.PTspace.indexOf(uword.charAt(i+1))]=cs.PTspace.indexOf(uword.charAt(i));
}
if (!encode(cs.PTspace).equals(decode(cs.PTspace)))
  throw new IllegalArgumentException("Invalid Stecker "+plugs);
}
// ---------------------------------------------------------------------
@Override
public String toString()
{
StringBuilder sb=new StringBuilder();
String plugEffect=encode(cs.PTspace);
if (plugEffect.equals(cs.PTspace)) sb.append("No steckerboard"+nL);
else {
  sb.append("Steckerboard : ");
  for (int i=0;i<plugEffect.length();i++)
    if (plugEffect.charAt(i)!=cs.PTspace.charAt(i) && 
        plugEffect.substring(0,i).indexOf(cs.PTspace.charAt(i))!=-1)
      sb.append(plugEffect.substring(i,i+1)+cs.PTspace.substring(i,i+1)+" ");
  sb.append(nL);
}
return sb.toString();
}
}
// ---------------------------------------------------------------------
protected class Rotor {

int roll1,roll2;
Wheel wheel;
String letters;
int position;
int ring;
int start;
int [] shiftL;
int [] shiftR;
// see http://en.wikipedia.org/w/index.php?title=Enigma_rotor_details&oldid=542507708
String [] stdWheels = { "ABCDEFGHIJKLMNOPQRSTUVWXYZ", // NULL wheel
    "DMTWSILRUYQNKFEJCAZBPGXOHV", // Commercial IC-A,B  
    "HQZGPJTMOBLNCIFDYAWVEUSRKX", // Commercial IIC-A,B  
    "UQNTLSZFMREHDPXKIBVYGJCWOA", // Commercial IIIC-A,B  
    "JGDQOXUSCAMIFRVTPNEWKBLZYH", // Rocket I
    "NTZPSFBOKMWRCJDIVLAEYUXHGQ", // Rocket II
    "JVIUBHTCDYAKEQZPOSGXNRMWFL", // Rocket III
    "PEZUOHXSCVFMTBGLRINQJWAYDK", // Swiss K-I
    "ZOUESYDKFWPCIQXHMVBLGNJRAT", // Swiss K-II
    "EHRVXGAOBQUSIMZFLYNWKTPDJC", // Swiss K-III
    "EKMFLGDQVZNTOWYHXUSPAIBRCJ",   // Enigma1-I
    "AJDKSIRUXBLHWTMCQGZNPYFVOE",   // Enigma1-II
    "BDFHJLCPRTXVZNYEIWGAKMUSQO",  // Enigma1-III
    "ESOVPZJAYQUIRHXLNFTGKDCMWB", // Enigma-IV
    "VZBRGITYUPSDNHLXAWMJQOFECK", // Enigma V
    "JPGVOUMFYQBENHZRDKASXLICTW", // Enigma VI
    "NZJHGRCXMYSWBOUFAIVLPEKQDT", // Enigma VII
    "FKQHTLXOCBJSPDZRAMEWNIUYGV", // Enigma VIII
    "LEYJVCNIXWPBQMDRTAKZGFUHOS", // Beta
    "FSOKANUERHMBTIYCWLQPZXVGJD"};// Gamma

String [] rolls={"A",
                 "Y","E","N",  // Commercial TBC
                 "N","E","Y",  // Rocket
                 "Y","E","N",  // Swiss K  
                 "Q","E","V","J","Z","ZM","ZM","ZM",  // Enigma std
                 "",""};  // Beta, Gamma - no turnover

// ---------------------------------------------------------------------
Rotor(Wheel wheel,String start,String ring,boolean reflected) 
{  
this(wheel,(int)(start.charAt(0)-65),(int)(ring.charAt(0)-65),reflected);
if (start.length()!=1) throw new
     IllegalArgumentException("Rotor start must be single letter");
if (ring.length()!=1) throw new
     IllegalArgumentException("Rotor ring position must be single letter");
}
// ---------------------------------------------------------------------
Rotor(Wheel wheel,int start,int ring,boolean reflected) {

int index;
this.start=start;
position=start;
this.ring=26-ring;  // Prevents need for later subtraction

this.wheel=wheel; // Mainly for toString()

if (start < 0 || start > 25) throw new IllegalArgumentException(
       "Rotor start must be 0-25 or single capital letter");

if (ring < 0 || ring > 25) throw new IllegalArgumentException(
       "Rotor ring position must be 0-25 or single capital letter");

switch (wheel) { 
  case NONE: index=0; break;  

  case CommercialAB_IC:    index=1; break;  
  case CommercialAB_IIC:   index=2; break;  
  case CommercialAB_IIIC:  index=3; break;  

  case Rocket_I:    index=4; break;  
  case Rocket_II:   index=5; break;  
  case Rocket_III:  index=6; break;
  
  case Swiss_KI:    index=7; break;  
  case Swiss_KII:   index=8; break;  
  case Swiss_KIII:  index=9; break;  

  case Enigma_I:    index=10; break;  
  case Enigma_II:   index=11; break;  
  case Enigma_III:  index=12; break;  
  case Enigma_IV:   index=13; break;  
  case Enigma_V:    index=14; break;  
  case Enigma_VI:   index=15; break;  
  case Enigma_VII:  index=16; break;  
  case Enigma_VIII: index=17; break;  

  case Beta:        index=18; break;  
  case Gamma:       index=19; break;  

  default:   throw new IllegalArgumentException("Unknown Rotor");
}

if (rolls[index].length()>0) roll1=(int)rolls[index].charAt(0)-65;
else roll1=(-1);

if (rolls[index].length()>1) roll2=(int)rolls[index].charAt(1)-65;
else                         roll2=roll1;

letters=stdWheels[index];

shiftL=new int[26];
shiftR=new int[26];
for (int i=0;i<26;i++) { 
  shiftL[i]=(26+(int)(stdWheels[index].charAt(i)-65)-i)%26;   // Guaranteed +ve
  shiftR[i]=reflected?(26+stdWheels[index].indexOf((char)(65+i))-i)%26:0; // Guaranteed +ve
}
}
// ---------------------------------------------------------------------
void reset()      { position=start; }
// ---------------------------------------------------------------------
boolean atRollover() 
        { position%=26;  return (position==roll1 || position==roll2); }
// ---------------------------------------------------------------------
void roll() { position++; }
// ---------------------------------------------------------------------
int permL(int index) { return index+shiftL[(index+position+ring)%26]; }
// ---------------------------------------------------------------------
int permR(int index) { return index+shiftR[(index+position+ring)%26];}
// ---------------------------------------------------------------------
}
protected class Reflector 
{
Reflect reflect;
int index;
boolean settable;
int setAt; // Only a few reflectors were settable - e.g. railway K

String [] stdReflectors = { "ABCDEFGHIJKLMNOPQRSTUVWXYZ", // NULL 
      "QYHOGNECVPUZTFDJAXWMKISRBL", // Railway - Rocket
      "IMETCGFRAYSQBZXWLHKDVUPOJN", // Swiss K
      "EJMZALYXVBWFCRQUONTSPIKHGD", // A
      "YRUHQSLDPXNGOKMIEBFZCWVJAT", // B
      "FVPJIAOYEDRZXWGCTKUQSBNMHL", // C
      "ENKQAUYWJICOPBLMDXZVFTHRGS", // B thin
      "RDOBJNTKVEHMLFCWZAXGYIPSUQ", // C thin
      "YRUHQSLDPXNGOKMIEBFZCWVJAT"};  // Typex 


int shift[]=new int[26];
// ---------------------------------------------------------------------
Reflector(Reflect reflect,String setting,String place)
{ // Deal with the rare settable reflectors : not sure this
  // is correct - uses both setting and ring offset - but does do 
  // Turing Treatise correctly

this(reflect);
settable=true;
if (setting.length() != 1) throw new IllegalArgumentException
   ("Settable reflector needs single character");

setAt=(52+(int)setting.charAt(0)-65-((int)place.charAt(0)-65))%26;
if (setAt < 0 || setAt > 25) throw new IllegalArgumentException
   ("Settable reflector needs character A-Z");
if (reflect!=Reflect.Rocket) throw new IllegalArgumentException
   ("Only rocket [so far] has settable reflector");
}
// ---------------------------------------------------------------------
Reflector(Reflect reflect)
{
this.reflect=reflect;
settable=false; // Overridden in other constructor
setAt=0;        // ditto

switch (reflect) { 
  case NONE:     index=0; break;  
  case Rocket:   index=1; break;  
  case Swiss_K:  index=2; break;  
  case A:        index=3; break;  
  case B:        index=4; break;  
  case C:        index=5; break;  
  case B_Thin:   index=6; break;  
  case C_Thin:   index=7; break; 
  case Typex:    index=8; break; 
  default:   throw new IllegalArgumentException("Unknown Reflector"); 
}
for (int i=0;i<26;i++) {
  shift[i]=(26+(int)stdReflectors[index].charAt(i)-65-i)%26;
}
}
// ---------------------------------------------------------------------
public String toString()
{
if (reflect==Reflect.NONE) return "No Reflector";

if (settable) return "Settable Reflector "+reflect+" "+
       stdReflectors[index]+" set at "+cs.PTspace.substring(setAt,setAt+1)+nL;

return "Reflector "+reflect+" "+stdReflectors[index]+nL;
}
// ---------------------------------------------------------------------
boolean noReflector() { return reflect==Reflect.NONE; } 
// ---------------------------------------------------------------------
int encode(int i) { return i+shift[(i+260+setAt)%26]; } // +260 so MOD works safely
}
// ---------------------------------------------------------------------
protected class Eintrittswalze extends Keyword
{
ETW etw;
int index;

String [] stdETWs = { "ABCDEFGHIJKLMNOPQRSTUVWXYZ", // NULL 
      "QWERTZUIOASDFGHJKPYXCVBNML", // Railway - Rocket
      "QWERTZUIOASDFGHJKPYXCVBNML"  // Swiss K
       }; 

int shift[]=new int[26];
// ---------------------------------------------------------------------
Eintrittswalze(ETW etw)
{
super(new Codespace(Codespace.StockAlphabet.CAPITALS));

this.etw=etw;

switch (etw) { 
  case NONE:     index=0; break;  
  case Rocket:   index=1; break;  
  case Swiss_K:  index=2; break;  
  default:   throw new IllegalArgumentException("Unknown Eintrittswalze"); 
}
makeKey(stdETWs[index]); 
}
// ---------------------------------------------------------------------
public String toString()
{
if (etw==ETW.NONE) return ("No/null Eintrittswalze"+nL);

return "Eintrittswalze "+etw+" "+stdETWs[index]+nL;
}
}
}