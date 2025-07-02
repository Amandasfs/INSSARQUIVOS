package inss.gca.mogi.mogi.mapper;

import inss.gca.mogi.mogi.dto.ServidorDTO;
import inss.gca.mogi.mogi.model.Servidor;

public class ServidorMapper {
    public static ServidorDTO toDto(Servidor servidor) {
        ServidorDTO dto = new ServidorDTO();
        dto.setId(servidor.getId());
        dto.setNome(servidor.getNome());
        dto.setMatricula(servidor.getMatricula());
        dto.setTipoPerfil(servidor.getTipoPerfil());
        dto.setStatusPerfil(servidor.isStatusPerfil());
        dto.setPodeCadastrar(servidor.isPodeCadastrar());
        dto.setPodeAlterar(servidor.isPodeAlterar());
        dto.setPodeAlterarNomeSegurado(servidor.isPodeAlterarNomeSegurado());
        dto.setPodeAlterarCaixa(servidor.isPodeAlterarCaixa());
        dto.setPodeAlterarCpfNb(servidor.isPodeAlterarCpfNb());
        dto.setPodeAlterarLocalCaixa(servidor.isPodeAlterarLocalCaixa());
        dto.setPodeAlterarCodigoCaixa(servidor.isPodeAlterarCodigoCaixa());
        dto.setPodeExcluir(servidor.isPodeExcluir());
        return dto;
    }

    public static Servidor toEntity(ServidorDTO dto) {
        Servidor servidor = new Servidor();
        servidor.setId(dto.getId());
        servidor.setNome(dto.getNome());
        servidor.setMatricula(dto.getMatricula());
        servidor.setTipoPerfil(dto.getTipoPerfil());
        servidor.setStatusPerfil(dto.isStatusPerfil());
        servidor.setPodeCadastrar(dto.isPodeCadastrar());
        servidor.setPodeAlterar(dto.isPodeAlterar());
        servidor.setPodeAlterarNomeSegurado(dto.isPodeAlterarNomeSegurado());
        servidor.setPodeAlterarCaixa(dto.isPodeAlterarCaixa());
        servidor.setPodeAlterarCpfNb(dto.isPodeAlterarCpfNb());
        servidor.setPodeAlterarLocalCaixa(dto.isPodeAlterarLocalCaixa());
        servidor.setPodeAlterarCodigoCaixa(dto.isPodeAlterarCodigoCaixa());
        servidor.setPodeExcluir(dto.isPodeExcluir());
        return servidor;
    }
}
