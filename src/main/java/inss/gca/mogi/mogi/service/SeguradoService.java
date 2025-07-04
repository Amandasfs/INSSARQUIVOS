package inss.gca.mogi.mogi.service;

import inss.gca.mogi.mogi.dao.SeguradoDAO;
import inss.gca.mogi.mogi.dto.SeguradoDTO;
import inss.gca.mogi.mogi.mapper.SeguradoMapper;
import inss.gca.mogi.mogi.model.Segurado;
import inss.gca.mogi.mogi.security.PermissaoValidator;
import inss.gca.mogi.mogi.service.exceptions.DataIntegrityViolationException;
import inss.gca.mogi.mogi.service.exceptions.ObjectNotFoundException;
import inss.gca.mogi.mogi.util.Sessao;

import java.util.List;
import java.util.stream.Collectors;

public class SeguradoService {

    private final SeguradoDAO seguradoDAO;

    public SeguradoService() {
        this.seguradoDAO = new SeguradoDAO();
    }

    public SeguradoDTO criarSegurado(SeguradoDTO seguradoDTO) {
        PermissaoValidator.validarPodeCadastrar();

        try {
            Segurado segurado = SeguradoMapper.toEntity(seguradoDTO);
            segurado.setIdServidor(Sessao.getServidor().getId());

            seguradoDAO.criar(segurado);
            return SeguradoMapper.toDto(segurado);

        } catch (Exception e) {
            throw new DataIntegrityViolationException("Erro ao criar segurado: " + e.getMessage());
        }
    }

    public SeguradoDTO atualizarNomeSegurado(int id, String novoNome) {
        PermissaoValidator.validarPodeAlterarNomeSegurado();

        try {
            seguradoDAO.atualizarNome(id, novoNome);
            Segurado segurado = seguradoDAO.buscarPorId(id);
            return SeguradoMapper.toDto(segurado);

        } catch (Exception e) {
            throw new DataIntegrityViolationException("Erro ao atualizar nome: " + e.getMessage());
        }
    }

    public SeguradoDTO buscarPorId(int id) {
        PermissaoValidator.validarPerfilAtivo();

        Segurado segurado = seguradoDAO.buscarPorId(id);
        if (segurado == null) {
            throw new ObjectNotFoundException("Segurado não encontrado");
        }
        return SeguradoMapper.toDto(segurado);
    }

    public List<SeguradoDTO> listarTodos() {
        PermissaoValidator.validarPerfilAtivo();

        return seguradoDAO.buscarTodos().stream()
                .map(SeguradoMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deletarSegurado(int id) {
        PermissaoValidator.validarPodeExcluir();

        try {
            seguradoDAO.deletar(id);
        } catch (Exception e) {
            throw new DataIntegrityViolationException("Erro ao excluir segurado: " + e.getMessage());
        }
    }
}