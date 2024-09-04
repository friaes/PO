package prr.app.visitors;

/**
 * Messages.
 */

interface Message {

  static String typeBasic() {
    return "BASIC";
  }
  
  static String typeFancy() {
    return "FANCY";
  }

  static String typeClient() {
    return "CLIENT";
  }

  static String typeText() {
    return "TEXT";
  }

  static String typeVoice() {
    return "VOICE";
  }

  static String typeVideo() {
    return "VIDEO";
  }

}