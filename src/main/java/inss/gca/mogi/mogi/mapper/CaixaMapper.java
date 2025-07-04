package inss.gca.mogi.mogi.mapper;

import inss.gca.mogi.mogi.dto.CaixaDTO;
import inss.gca.mogi.mogi.model.Caixa;

public class CaixaMapper {
    public static CaixaDTO toDto(Caixa caixa) {
        if (caixa == null) return null;

        CaixaDTO dto = new CaixaDTO();
        dto.setCodCaixa(caixa.getCodCaixa());
        dto.setPrateleira(caixa.getPrateleira());
        dto.setRua(caixa.getRua());
        dto.setAndar(caixa.getAndar());
        dto.setNbInicial(caixa.getNbInicial());
        dto.setNbFinal(caixa.getNbFinal());
        dto.setIdServidor(caixa.getIdServidor());
        return dto;
    }

    public static Caixa toEntity(CaixaDTO dto) {
        if (dto == null) return null;

        Caixa caixa = new Caixa();
        caixa.setCodCaixa(dto.getCodCaixa());
        caixa.setPrateleira(dto.getPrateleira());
        caixa.setRua(dto.getRua());
        caixa.setAndar(dto.getAndar());
        caixa.setNbInicial(dto.getNbInicial());
        caixa.setNbFinal(dto.getNbFinal());
        caixa.setIdServidor(dto.getIdServidor());
        return caixa;
    }
}