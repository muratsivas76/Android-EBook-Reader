package net.murat.ebook;

import net.murat.sayfas.*;

public class Adding {
  
  public Adding() {
    super();
  }
  
  private void grup1() {
    AInfos.addSayfa(new Sayfa_00001());
    AInfos.addSayfa(new Sayfa_00002());
    AInfos.addSayfa(new Sayfa_00003());
    AInfos.addSayfa(new Sayfa_00004());
    AInfos.addSayfa(new Sayfa_00005());
  }
  
  public void addAll() {
    grup1();
  }
  
}
