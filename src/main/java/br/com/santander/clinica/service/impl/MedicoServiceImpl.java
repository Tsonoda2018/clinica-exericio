package br.com.santander.clinica.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import br.com.santander.clinica.model.Agenda;
import br.com.santander.clinica.model.Especialidade;
import br.com.santander.clinica.model.Medico;
import br.com.santander.clinica.model.dto.AgendaDto;
import br.com.santander.clinica.model.dto.AgendaInputDto;
import br.com.santander.clinica.model.dto.AgendaPacienteDto;
import br.com.santander.clinica.model.dto.FiltroAgendaDto;
import br.com.santander.clinica.model.dto.MedicoFiltroDto;
import br.com.santander.clinica.repository.MedicoRepository;
import br.com.santander.clinica.repository.specification.MedicoSpecification;
import br.com.santander.clinica.service.MedicoService;

@Service
public class MedicoServiceImpl implements MedicoService {

	private final MedicoRepository medicoRepository;
	private final AgendaServiceImpl agendaService;

	public MedicoServiceImpl(MedicoRepository medicoRepository, AgendaServiceImpl agendaService) {
		super();
		this.medicoRepository = medicoRepository;
		this.agendaService = agendaService;
	}
	

	@Override
	public Medico salvar(Medico medico) {
		return this.medicoRepository.save(medico);
	}


	@Override
	public Medico buscarPorId(Integer id) {
		return this.medicoRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Não existe médico com o id " + id));
	}

	@Override
	public void excluir(Integer id) {
		this.medicoRepository.deleteById(id);

	}

	@Override
	public List<Medico> buscarPorEspecialidade(Especialidade especialidade) {
		return this.medicoRepository.findAllByEspecialidadeId(especialidade.getid());
	}

	@Override
	public List<AgendaDto> liberarAgenda(AgendaInputDto agendaInputDto) {
		Medico medico = this.buscarPorId(agendaInputDto.getIdMedico());
		List<AgendaDto> agenda = agendaService.liberarAgenda(medico, agendaInputDto.getData());
		return agenda;

	}

	@Override
	public List<AgendaDto> consultarAgenda(Medico medico) {
		List<Agenda> agenda = agendaService.buscarTodos(new FiltroAgendaDto(medico.getNome(), null, medico.getEspecialidade().getNome())); 
		return agenda.stream().map(a -> AgendaDto.converte(a)).collect(Collectors.toList());
//		return agendaService.buscarAgendaPorMedico(medico).stream().map(a -> AgendaDto.converte(a))
//				.collect(Collectors.toList());
	}

	@Override
	public List<AgendaPacienteDto> consutarPacientePorData(Medico medico, LocalDate data) {
		List<AgendaPacienteDto> dto = agendaService.buscarAgendaPorData(medico, data);
		return dto;
	}


	@Override
	public List<Medico> buscarTodos(MedicoFiltroDto filtro) {
		List<Medico> findAll = medicoRepository.findAll(Specification.where(MedicoSpecification.porNomeMedico(filtro.getNomeMedico())
				.or(MedicoSpecification.porEspecialidade(filtro.getEspecialidade()))));
		return findAll;
	}

}
