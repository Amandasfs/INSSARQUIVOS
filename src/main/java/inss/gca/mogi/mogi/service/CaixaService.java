package inss.gca.mogi.mogi.service;

import inss.gca.mogi.mogi.dao.CaixaDAO;
import inss.gca.mogi.mogi.dto.CaixaDTO;
import inss.gca.mogi.mogi.mapper.CaixaMapper;
import inss.gca.mogi.mogi.model.Caixa;
import inss.gca.mogi.mogi.security.PermissaoValidator;
import inss.gca.mogi.mogi.service.exceptions.DataIntegrityViolationException;
import inss.gca.mogi.mogi.service.exceptions.ObjectNotFoundException;
import inss.gca.mogi.mogi.util.Sessao;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CaixaService {

    private final CaixaDAO caixaDAO;

    public CaixaService() {
        this.caixaDAO = new CaixaDAO();
    }

    public CaixaDTO criarCaixa(CaixaDTO caixaDTO) {
        PermissaoValidator.validarPodeCadastrar();

        try {
            Caixa caixa = CaixaMapper.toEntity(caixaDTO);
            caixa.setIdServidor(Sessao.getServidor().getId());

            caixaDAO.criar(caixa);
            return CaixaMapper.toDto(caixa);

        } catch (Exception e) {
            throw new DataIntegrityViolationException("Erro ao criar caixa: " + e.getMessage());
        }
    }

    public CaixaDTO atualizarLocalizacao(String codCaixa, String novoLocal) {
        PermissaoValidator.validarPodeAlterarLocalCaixa();

        try {
            caixaDAO.atualizarLocalizacao(codCaixa, novoLocal);
            Optional<Caixa> caixaOpt = caixaDAO.buscarCaixaPorCodigo(codCaixa);

            if (caixaOpt.isEmpty()) {
                throw new ObjectNotFoundException("Caixa não encontrada após atualização");
            }

            return CaixaMapper.toDto(caixaOpt.get());

        } catch (Exception e) {
            throw new DataIntegrityViolationException("Erro ao atualizar localização: " + e.getMessage());
        }
    }

    public List<CaixaDTO> listarTodas() {
        PermissaoValidator.validarPerfilAtivo();

        return caixaDAO.buscarTodas().stream()
                .map(CaixaMapper::toDto)
                .collect(Collectors.toList());
    }

    public CaixaDTO buscarPorCodigo(String codCaixa) {
        PermissaoValidator.validarPerfilAtivo();

        Optional<Caixa> caixaOpt = caixaDAO.buscarCaixaPorCodigo(codCaixa);
        if (caixaOpt.isEmpty()) {
            throw new ObjectNotFoundException("Caixa não encontrada");
        }
        return CaixaMapper.toDto(caixaOpt.get());
    }
}