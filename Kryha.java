public class Kryha extends ClassicalCipher
{
// Conducts encryption and decryption per a Polyalphabetic process based 
// on a Kryha Cipher machine.
// See Q.E.D. 2 Hours, 41 Minutes BY LAMBROS D. CALLIMAHOS

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

public String keyP;    
public String keyC;    
private int place;    // Place in sequence
private int [] adv;

// ----------------------------------------------------------------------
Kryha(String wordP,String wordC,int [] pins)
{
this(new Codespace(Codespace.StockAlphabet.CAPITALS),wordP,wordC,pins); 
}
// ----------------------------------------------------------------------
Kryha(Codespace cs,String wordP,String wordC,int [] pins)
{
super(cs);
keyP=new Keyword().new Simple(cs,wordP).getKey();
keyC=new Keyword().new Simple(cs,wordC).getKey();

place=0;
adv=new int[pins.length];
for (int i=0;i<pins.length;i++) adv[i]=pins[i];
}
// --------------------------------------------------------------
public String toString() 
     { return ("Kryha Cipher : "+nL+"Plain ="+keyP+nL+
         "Cipher="+keyC+super.toString());}
// ----------------------------------------------------------------------
@Override
public String encode(String PT) { 

String flat=cs.flattenToPT(PT);
StringBuilder sb=new StringBuilder(flat.length());

place=0;
for (int i=0;i<flat.length();i++) {
  sb.append(keyC.charAt(
      (keyP.indexOf(flat.charAt(i))+place)%cs.PTspace.length()));
  place=(place+adv[i%adv.length])%cs.PTspace.length();
}
return sb.toString();
}
// ----------------------------------------------------------------------
@Override
public String decode(String CT) { 

String flat=cs.flattenToCT(CT);
StringBuilder sb=new StringBuilder(flat.length());

place=0;
for (int i=0;i<flat.length();i++) {
  sb.append(keyP.charAt(
              (cs.PTspace.length()+keyC.indexOf(flat.charAt(i))-
               place)%cs.PTspace.length()));
  place=(place+adv[i%adv.length])%cs.PTspace.length();
}
return sb.toString();
}
// ----------------------------------------------------------------------
public static void main(String [] args)
{
// Test challenge to Friedman in 1933
String CT="XYICPNDEAMAPDTRAXXPZXHYRYTWQXFHCDJKAHQURZPPPZQOFUVKFEMNEAONGTT"+
"XSVVUBDGJREJFHEOKVCQHFHROKUPMQPQWACOJCRLMBMEVKRVJDYNNSXUDLHNPFWMOCMJFLGP"+
"MBKHAUXLIVVQSXUNJZUKKOBAAEUQOYJIZSZUHGWGWATEJWYDIVXPEIKEECMCIRXXLAZLAINM"+
"JZXICIDKQLMMTELLFJTJUBQOLJAWMFEHEVSYCASKFONOZUMPADAPJYLPFNTRUITCBWHJHMOL"+
"CVRDEPFQACIUHCZCBXTOKCIXGOSGCMRFHJVXSVZNMUGJJSOQBJQHBQNLHRTMELYNHKUFXJDM"+
"JYCPADPPWYMGUWOIAIIGPTSFCSOKIDGGTYOAAQDRQRRMNTSHYNEXYVFCMJJKNXVTEFXAUTSE"+
"ZQSHLULYCYGXONLAWQTEJNBSMVTEHSXUYNJKXFPEPGFCMMCWZRPJYGOPZUQNVXIAXZKQMJEF"+
"WWMRQRTETPXRSUKCDLHEDLLCTJKSZMQMKNJUVPFLYHYFQREWNDZMBMPBOJXEQIZAXHNDBQQW"+
"DIXQPIFAYJGQJOFWFCDBXYNXYTWYKEQCDPDYDOZHJFCZUEDDJBFXTTVFYGHCTBGOFEHBUBZD"+
"QQTIGDYAIYFDFHABSAHYGXIBBLECQOSEMOMZVKHQSJCMJFFEVVTLWTESLAYWFYCKOXPSVNAI"+
"GOCZZKVVVJSOPENYXDDXLDCYAXMWWOCWOIIBNXTVTLIVQWXUETPSUHCSOYTPVYIKZNFVIEYP"+
"HKINCGGVIKROOSOVMGHKUNUSUNYVCFELOOWSAIYRREVNEXPESEGRPZNBMMYUZFGSXRXWMNWT"+
"LRHVFHGSXMWVREAJDGOZAGRXKJLDOGYPTYXNTMWQMYSQWLXHNGZQDMCWPYATGNZFJKWGDKAV"+
"SJMHJGWJECWTDBZNMYTNAORVHARRPDXGCAPHJNZKTLQRQJJAFFZGDXLRFFSAWSZNGLSAQBMC"+
"DYJFMBLSXEOTLFJGGLGKKRYYWDALHHJVCGYVRLYSPJVPKGWWXHFACMTRGUJEJWTAFSNZXVVW"+
"IYWOOMTLUFSBCAJRNRMPIYLWIKAOKHTMXCNIMWTFGTTDEHTDHMKKCDKEAPHIAXZYP";

String PT="THECOURTISUNABLETHEREFORETOPERCEIVETHEPRESENCEINTHEINSTANTCASE"+
"OFANYCIRCUMSTANCEWHICHMIGHTDIFFERENTIATEITFROMTHECASESINVOLVINGTHEUSEOFT"+
"HERUSSIANSEALDECIDEDBYJUDGEANDREWSINTHISSITUATIONTHECOURTISUNWILLINGTORE"+
"ACHACONCLUSIONDIAMETRICALLYOPPOSEDTOTHATARRIVEDATBYTHATABLEJURISTAFTERTH"+
"ECAREFULANDPAINSTAKINGCONSIDERATIONOFTHELAWANDTHEFACTSWHICHHISLEARNEDOPI"+
"NIONINDICATESHEGAVETOTHEACTIONSTRIEDBEFOREHIMTHECOURTACCORDINGLYHOLDSTHA"+
"TTHEREISINSUFFICIENTPROOFINTHEPRESENTRECORDTOSUSTAINAFINDINGTHATTHERUSSI"+
"ANBUSINESSORTHATITSAGENTSWEREEMPOWEREDTOAFFIXITTOPOLICIESSOASTOGIVETHEMT"+
"HEFORCEANDEFFECTOFSEALEDINSTRUMENTSTHEFIRSTCAUSEOFACTIONISTHEREFOREBARRE"+
"DBYOURSIXYEARSTATUTEOFLIMITATIONSANDITISACCORDINGLYUNNECESSARYTOCONSIDER"+
"WHETHERTHERUSSIANSTATUTEOFLIMITATIONSOFTENYEARSCOMMENCEDTORUNUPONTHEACCR"+
"UALOFTHECAUSEOFACTIONASTHEDEFENDANTCONTENDSORUPONTHECOMMENCEMENTOFTHEACT"+
"IONASTHEPLAINTIFFARGUESITISLIKEWISEUNNECESSARYTODETERMINEWHETHERTHERUNNI"+
"NGOFTHISSTATUTEWASSUSPENDEDBECAUSEOFTHECLOSINGOFTHERUSSIANCOURTSTOCONTRO"+
"VERSIESARISINGOUTOFCIVILRELATIONSHIPORIGINATINGPRIORTOTHESOVIETREVOLUTIO"+
"NTHEMOTIONTODISMISSTHECOMPLAINTISGRANTEDASTOTHEFIRSTCAUSEOFACTION";

int [] pins={6,5,9,7,6,7,5,6,7,6,8,6,10,5,6,5,7};  
Kryha kryha=new Kryha("PLMJNHGIBAKETCDQXSWVURFOZY","JNFGHEACBDWYXZVURSTQLOIMPK",pins);
// Keys from 1933 challenge
// Note that Plain key Q and Z can be reversed since do not appear in plaintext

//System.out.println(kryha);

if (kryha.knownTest(PT,CT)) System.out.println("PASS");
else System.out.println("******** FAIL ***********");


}
}