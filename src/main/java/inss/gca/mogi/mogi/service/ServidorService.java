package inss.gca.mogi.mogi.service;

import inss.gca.mogi.mogi.dao.ServidorDAO;
import inss.gca.mogi.mogi.dto.ServidorDTO;
import inss.gca.mogi.mogi.mapper.ServidorMapper;
import inss.gca.mogi.mogi.model.Servidor;
import inss.gca.mogi.mogi.security.PermissaoValidator;
import inss.gca.mogi.mogi.service.exceptions.DataIntegrityViolationException;
import inss.gca.mogi.mogi.service.exceptions.ObjectNotFoundException;
import inss.gca.mogi.mogi.config.PasswordUtil;
import inss.gca.mogi.mogi.util.Sessao;

public class ServidorService {

    private final ServidorDAO servidorDAO;

    public ServidorService() {
        this.servidorDAO = new ServidorDAO();
    }

    public ServidorDTO criarServidor(ServidorDTO servidorDTO) {
        PermissaoValidator.validarGerente();

        try {
            Servidor servidor = ServidorMapper.toEntity(servidorDTO);
            servidor.setSenha(PasswordUtil.hashPassword(servidorDTO.getSenha()));

            servidorDAO.criarServidor(servidor, Sessao.getServidor().getId());
            return ServidorMapper.toDto(servidor);

        } catch (Exception e) {
            throw new DataIntegrityViolationException("Erro ao criar servidor: " + e.getMessage());
        }
    }

    public ServidorDTO atualizarServidor(ServidorDTO servidorDTO) {
        PermissaoValidator.validarGerente();

        try {
            Servidor servidorExistente = servidorDAO.buscarPorId(servidorDTO.getId());
            Servidor servidorAtualizado = ServidorMapper.toEntity(servidorDTO);

            // Mantém a senha original se não foi alterada
            if (servidorDTO.getSenha() == null || servidorDTO.getSenha().isEmpty()) {
                servidorAtualizado.setSenha(servidorExistente.getSenha());
            } else {
                servidorAtualizado.setSenha(PasswordUtil.hashPassword(servidorDTO.getSenha()));
            }

            servidorDAO.atualizarServidor(servidorAtualizado, Sessao.getServidor().getId());
            return ServidorMapper.toDto(servidorAtualizado);

        } catch (Exception e) {
            throw new DataIntegrityViolationException("Erro ao atualizar servidor: " + e.getMessage());
        }
    }

    public ServidorDTO buscarPorMatricula(int matricula) {
        PermissaoValidator.validarPerfilAtivo();

        Servidor servidor = servidorDAO.buscarPorMatricula(matricula);
        if (servidor == null) {
            throw new ObjectNotFoundException("Servidor não encontrado");
        }
        return ServidorMapper.toDto(servidor);
    }

    public void atualizarPermissoes(int servidorId, ServidorDTO permissoesDTO) {
        PermissaoValidator.validarGerente();

        try {
            Servidor servidor = servidorDAO.buscarPorId(servidorId);

            // Atualiza apenas as permissões
            servidor.setPodeCadastrar(permissoesDTO.isPodeCadastrar());
            servidor.setPodeAlterar(permissoesDTO.isPodeAlterar());
            servidor.setPodeAlterarNomeSegurado(permissoesDTO.isPodeAlterarNomeSegurado());
            servidor.setPodeAlterarCaixa(permissoesDTO.isPodeAlterarCaixa());
            servidor.setPodeAlterarCpfNb(permissoesDTO.isPodeAlterarCpfNb());
            servidor.setPodeAlterarLocalCaixa(permissoesDTO.isPodeAlterarLocalCaixa());
            servidor.setPodeAlterarCodigoCaixa(permissoesDTO.isPodeAlterarCodigoCaixa());
            servidor.setPodeExcluir(permissoesDTO.isPodeExcluir());

            servidorDAO.atualizarPermissoes(servidor, Sessao.getServidor().getId());

        } catch (Exception e) {
            throw new DataIntegrityViolationException("Erro ao atualizar permissões: " + e.getMessage());
        }
    }

    public ServidorDTO autenticar(String matricula, String senha) {
        try {
            Servidor servidor = servidorDAO.autenticar(matricula, senha);
            if (servidor == null) {
                throw new ObjectNotFoundException("Matrícula ou senha inválidos");
            }
            return ServidorMapper.toDto(servidor);

        } catch (Exception e) {
            throw new DataIntegrityViolationException("Erro ao autenticar: " + e.getMessage());
        }
    }
}