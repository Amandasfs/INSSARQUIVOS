package inss.gca.mogi.mogi.service;

import inss.gca.mogi.mogi.dao.ArquivoDAO;
import inss.gca.mogi.mogi.dto.ArquivoDTO;
import inss.gca.mogi.mogi.mapper.ArquivoMapper;
import inss.gca.mogi.mogi.model.Arquivo;
import inss.gca.mogi.mogi.security.PermissaoValidator;
import inss.gca.mogi.mogi.service.exceptions.DataIntegrityViolationException;
import inss.gca.mogi.mogi.util.Sessao;

import java.util.List;
import java.util.stream.Collectors;

public class ArquivoService {

    private final ArquivoDAO arquivoDAO;

    public ArquivoService() {
        this.arquivoDAO = new ArquivoDAO();
    }

    public ArquivoDTO criarArquivo(ArquivoDTO arquivoDTO) {
        PermissaoValidator.validarPodeCadastrar();

        try {
            Arquivo arquivo = ArquivoMapper.toEntity(arquivoDTO);
            arquivo.setIdServidor(Sessao.getServidor().getId());

            arquivoDAO.criar(arquivo);
            return ArquivoMapper.toDto(arquivo);

        } catch (Exception e) {
            throw new DataIntegrityViolationException("Erro ao criar arquivo: " + e.getMessage());
        }
    }

    public ArquivoDTO atualizarNB(int id, String novoNB) {
        PermissaoValidator.validarPodeAlterarCpfNb();

        try {
            arquivoDAO.atualizarNB(id, novoNB);
            Arquivo arquivo = arquivoDAO.buscarPorId(id);
            return ArquivoMapper.toDto(arquivo);

        } catch (Exception e) {
            throw new DataIntegrityViolationException("Erro ao atualizar NB: " + e.getMessage());
        }
    }

    public List<ArquivoDTO> buscarPorSegurado(int idSegurado) {
        PermissaoValidator.validarPerfilAtivo();

        return arquivoDAO.buscarPorSegurado(idSegurado).stream()
                .map(ArquivoMapper::toDto)
                .collect(Collectors.toList());
    }

    public void moverArquivo(int idArquivo, String novoCodCaixa) {
        PermissaoValidator.validarPodeAlterarCaixa();

        try {
            arquivoDAO.moverArquivo(idArquivo, novoCodCaixa);
        } catch (Exception e) {
            throw new DataIntegrityViolationException("Erro ao mover arquivo: " + e.getMessage());
        }
    }
}