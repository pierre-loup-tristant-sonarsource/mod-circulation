package org.folio.circulation.loanrules;

public class PatronGroup {
  public String id;
  public PatronGroup(String id) {
    this.id = id;
  }
  @Override
  public String toString() {
    return id;
  }
}