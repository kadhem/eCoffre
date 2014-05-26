package edu.esprit.eCoffreEJB.Technique;


import org.smslib.*;

class SendMessage {

    public static void main(String[] args) {

        CService srv = new CService("COM6", 115200, "Wiko", "");




        System.out.println();

        try {

            srv.setSimPin("0000");
            srv.setSmscNumber("+21652014417");
            srv.connect();          
            COutgoingMessage msg = new COutgoingMessage("22936773", "bonjour esprit.");
            msg.setMessageEncoding(CMessage.MESSAGE_ENCODING_7BIT);
            msg.setStatusReport(false);
            msg.setFlashSms(false);
            msg.setValidityPeriod(8);
            srv.sendMessage(msg);
            srv.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
