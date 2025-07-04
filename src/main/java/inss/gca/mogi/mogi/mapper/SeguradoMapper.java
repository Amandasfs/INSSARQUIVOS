package inss.gca.mogi.mogi.mapper;

import inss.gca.mogi.mogi.dto.SeguradoDTO;
import inss.gca.mogi.mogi.model.Segurado;

public class SeguradoMapper {
    public static SeguradoDTO toDto(Segurado segurado) {
        if (segurado == null) return null;

        SeguradoDTO dto = new SeguradoDTO();
        dto.setId(segurado.getId());
        dto.setNomeSegurado(segurado.getNomeSegurado());
        dto.setCpf(segurado.getCpf());
        dto.setIdServidor(segurado.getIdServidor());
        return dto;
    }

    public static Segurado toEntity(SeguradoDTO dto) {
        if (dto == null) return null;

        Segurado segurado = new Segurado();
        segurado.setId(dto.getId());
        segurado.setNomeSegurado(dto.getNomeSegurado());
        segurado.setCpf(dto.getCpf());
        segurado.setIdServidor(dto.getIdServidor());
        return segurado;
    }
}