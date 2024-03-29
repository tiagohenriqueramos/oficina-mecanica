package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listener.DataChangeListener;
import gui.utils.Alerts;
import gui.utils.Constraints;
import gui.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Cliente;
import model.entities.Veiculo;
import model.exceptions.ValidationException;
import model.services.ClienteService;
import model.services.VeiculoService;

public class ClienteFormulario implements Initializable {

	Cliente entidade;
	Veiculo veiculo;
	
	VeiculoService veiculoService;

	ClienteService clienteService;
	

	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private Label labelErrorNome;

	@FXML
	private Label labelErrorId;

	@FXML
	private Label labelErrorCpf;

	@FXML
	private Label labelErrorTelefone;

	@FXML
	private Label labelErrorEndereco;
	

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtNome;

	@FXML
	private TextField txtCpf;

	@FXML
	private TextField txtTelefone;

	@FXML
	private TextField txtEndereco;
	

	@FXML
	private Button btSalvar;

	@FXML
	private Button btCancelar;

	public void setClienteService(ClienteService clienteService) {
		this.clienteService = clienteService;
	}

	public void setEntidade(Cliente cliente) {
		this.entidade = cliente;
	}

	@FXML
	public void onBtSalvar(ActionEvent event) {

		if (entidade == null) {
			throw new IllegalStateException("Entidade nula!");
		}
		if (clienteService == null) {
			throw new IllegalStateException("Service nulo!");
		}
		try {

			entidade = getFormData();
			clienteService.saveOrUpdate(entidade);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();

		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		} catch (DbException e) {
			Alerts.showAlert("Erro ao salvar objeto", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}

	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	public void subscribeDataChengeListener(DataChangeListener listner) {
		dataChangeListeners.add(listner);
	}

	private Cliente getFormData() {
		Cliente cliente = new Cliente();
		

		ValidationException exception = new ValidationException("Erro de valida��o!");

		cliente.setId(Utils.tryParseToInt(txtId.getText()));
		
		if (txtNome.getText() == null || txtNome.getText().trim().equals("")) {
			exception.addError("Nome", "O campo n�o pode ser vazio!");
		}
		cliente.setNome(txtNome.getText());

		if (txtCpf.getText() == null || txtCpf.getText().trim().equals("")) {
			exception.addError("Cpf", "O campo n�o pode ser vazio!");

		} else if (txtCpf.getText() != null && !txtCpf.getText().matches("((\\d{3}).(\\d{3}).(\\d{3})-(\\d{2}))")) {
			exception.addError("Cpf", "Padr�o inv�lido!");

		}

		cliente.setCpf(txtCpf.getText());

		if (txtTelefone.getText() == null || txtTelefone.getText().trim().equals("")) {
			exception.addError("Telefone", "O campo n�o pode ser vazio!");

		} else if (txtTelefone.getText() != null
				&& !txtTelefone.getText().matches("(\\(\\d{2}\\))(\\d{4,5}\\-\\d{4})")) {
			exception.addError("Telefone", "Padr�o inv�lido!");

		}

		cliente.setTelefone(txtTelefone.getText());

		if (txtEndereco.getText() == null || txtEndereco.getText().trim().equals("")) {
			exception.addError("Endereco", "O campo n�o pode ser vazio!");

		}
		cliente.setEndereco(txtEndereco.getText());

		if (exception.getErrors().size() > 0) {
			throw exception;
		}
		cliente.setId(Utils.tryParseToInt(txtId.getText()));

		
	

		return cliente;
	}

	

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldCpf(txtCpf);
		Constraints.setTextFieldTelefone(txtTelefone);
		Constraints.setTextFieldMaxLength(txtNome, 255);
		
	}

	public void updateFormData() {
		if (entidade == null) {
			throw new IllegalStateException("Entidade nula!");
		}
		txtId.setText(String.valueOf(entidade.getId()));
		txtCpf.setText(entidade.getCpf());
		txtNome.setText(entidade.getNome());
		txtEndereco.setText(entidade.getEndereco());
		txtTelefone.setText(entidade.getTelefone());
		

	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();

		if (fields.contains("Nome")) {
			labelErrorNome.setText(errors.get("Nome"));

		} else {
			labelErrorNome.setText("");
		}

		if (fields.contains("Cpf")) {
			labelErrorCpf.setText(errors.get("Cpf"));

		} else {
			labelErrorCpf.setText("");
		}

		if (fields.contains("Telefone")) {
			labelErrorTelefone.setText(errors.get("Telefone"));

		} else {
			labelErrorTelefone.setText("");
		}

		if (fields.contains("Endereco")) {
			labelErrorEndereco.setText(errors.get("Endereco"));

		} else {
			labelErrorEndereco.setText("");
		}
		
		}
}
