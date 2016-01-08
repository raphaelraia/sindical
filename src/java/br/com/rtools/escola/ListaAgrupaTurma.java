package br.com.rtools.escola;

public class ListaAgrupaTurma {

    private AgrupaTurma agrupaTurma;
    private Boolean isIntegral;

    public ListaAgrupaTurma() {
        this.agrupaTurma = new AgrupaTurma();
        this.isIntegral = false;
    }

    public ListaAgrupaTurma(AgrupaTurma agrupaTurma, Boolean isIntegral) {
        this.agrupaTurma = agrupaTurma;
        this.isIntegral = isIntegral;
    }

    public AgrupaTurma getAgrupaTurma() {
        return agrupaTurma;
    }

    public void setAgrupaTurma(AgrupaTurma agrupaTurma) {
        this.agrupaTurma = agrupaTurma;
    }

    public Boolean getIsIntegral() {
        return isIntegral;
    }

    public void setIsIntegral(Boolean isIntegral) {
        this.isIntegral = isIntegral;
    }

}
