package inss.gca.mogi.mogi.mapper;

import inss.gca.mogi.mogi.dto.ServidorDTO;
import inss.gca.mogi.mogi.model.Servidor;

public class ServidorMapper {
    public static ServidorDTO toDto(Servidor servidor) {
        if (servidor == null) return null;

        ServidorDTO dto = new ServidorDTO();
        dto.setId(servidor.getId());
        dto.setNome(servidor.getNome());
        dto.setMatricula(servidor.getMatricula());
        dto.setTipoPerfil(servidor.getTipoPerfil());
        dto.setStatusPerfil(servidor.isStatusPerfil());

        // Permissões - apenas para visualização (gerentes podem editar)
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
        if (dto == null) return null;

        Servidor servidor = new Servidor(
                dto.getId(),
                dto.getNome(),
                "", // Senha deve ser tratada separadamente
                dto.getMatricula(),
                dto.getTipoPerfil()
        );

        // Apenas atualiza status e permissões se não for novo registro
        if (dto.getId() != 0) {
            servidor.setStatusPerfil(dto.isStatusPerfil());

            // Permissões só podem ser alteradas por gerentes
            servidor.setPodeCadastrar(dto.isPodeCadastrar());
            servidor.setPodeAlterar(dto.isPodeAlterar());
            servidor.setPodeAlterarNomeSegurado(dto.isPodeAlterarNomeSegurado());
            servidor.setPodeAlterarCaixa(dto.isPodeAlterarCaixa());
            servidor.setPodeAlterarCpfNb(dto.isPodeAlterarCpfNb());
            servidor.setPodeAlterarLocalCaixa(dto.isPodeAlterarLocalCaixa());
            servidor.setPodeAlterarCodigoCaixa(dto.isPodeAlterarCodigoCaixa());
            servidor.setPodeExcluir(dto.isPodeExcluir());
        }

        return servidor;
    }

    // Método especial para atualização de senha
    public static Servidor toEntityWithPassword(ServidorDTO dto, String encryptedPassword) {
        Servidor servidor = toEntity(dto);
        if (servidor != null) {
            servidor.setSenha(encryptedPassword);
        }
        return servidor;
    }
}