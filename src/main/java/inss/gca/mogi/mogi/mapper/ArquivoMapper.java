package inss.gca.mogi.mogi.mapper;

import inss.gca.mogi.mogi.dto.ArquivoDTO;
import inss.gca.mogi.mogi.model.Arquivo;

public class ArquivoMapper {
    public static ArquivoDTO toDto(Arquivo arquivo) {
        if (arquivo == null) return null;

        ArquivoDTO dto = new ArquivoDTO();
        dto.setId(arquivo.getId());
        dto.setNb(arquivo.getNb());
        dto.setTipoBeneficio(arquivo.getTipoBeneficio());
        dto.setIdSegurado(arquivo.getIdSegurado());
        dto.setCodCaixa(arquivo.getCodCaixa());
        dto.setIdServidor(arquivo.getIdServidor());
        return dto;
    }

    public static Arquivo toEntity(ArquivoDTO dto) {
        if (dto == null) return null;

        Arquivo arquivo = new Arquivo();
        arquivo.setId(dto.getId());
        arquivo.setNb(dto.getNb());
        arquivo.setTipoBeneficio(dto.getTipoBeneficio());
        arquivo.setIdSegurado(dto.getIdSegurado());
        arquivo.setCodCaixa(dto.getCodCaixa());
        arquivo.setIdServidor(dto.getIdServidor());
        return arquivo;
    }
}