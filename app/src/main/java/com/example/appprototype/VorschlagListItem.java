package com.example.appprototype;

public class VorschlagListItem
{
    private String vorschlag;
    private boolean checked;

    public VorschlagListItem(String vorschlag, boolean checked) {
        this.vorschlag = vorschlag;
        this.checked = checked;
    }

    public String getVorschlag() {
        return vorschlag;
    }

    public void setVorschlag(String vorschlag) {
        this.vorschlag = vorschlag;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
