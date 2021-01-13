package app.controller;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.PatternSyntaxException;

import javax.xml.transform.TransformerFactoryConfigurationError;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

public class FXMLDocumentController implements Initializable {

	@FXML
	private TextField txtfield_url;

	@FXML
	private ComboBox<String> combo_method;

	@FXML
	private JFXButton btn_copy;

	@FXML
	private Button btn_send;

	@FXML
	private JFXTextArea textarea_result;

	@FXML
	private Text txt_error;

	@FXML
	private Text txt_status;


	@Override
	public void initialize(URL location, ResourceBundle rb) {
		txtfield_url.setText("http://localhost:5000/api/users");

		combo_method.getItems().add("GET");
		combo_method.getItems().add("POST");
	}


	/*
	 * COPY button
	 */
	@FXML
	void onBtn_copyMouseClicked(MouseEvent event) {
		Toolkit.getDefaultToolkit()
		.getSystemClipboard()
		.setContents(
				new StringSelection(textarea_result.getText()),
				null
				);
	}


	/*
	 * Send request button
	 */
	@FXML
	void onBtn_sendMouseClicked(MouseEvent event) {
		if (txtfield_url.getText().length() <= 5) {
			txt_error.setText("Veuillez spécifier une URL");// error message under send button
			return;
		}

		// Reset error message
		txt_error.setText("");
		try {
			String result = null;
			String combo_value = combo_method.getValue();

			// If no method selected
			if (combo_value == null) {
				textarea_result.setText("");
				txt_error.setText("Veuillez spécifier une méthode d'envoi");
				return;
			}

			RequestBuilder requestBuilder = null;
			// Build request
			if (combo_value.equalsIgnoreCase("GET")) {
				requestBuilder = new RequestBuilder(txtfield_url.getText(), RequestType.GET);
			} else if (combo_value.equalsIgnoreCase("POST")) {
				requestBuilder = new RequestBuilder(txtfield_url.getText(), RequestType.POST);
			}

			if (requestBuilder != null) {
				// Send request
				requestBuilder.sendRequest();
				// Get request response
				result = requestBuilder.getResponse();
				txt_status.setText(String.valueOf(requestBuilder.getResponseCode()));
			}

			if (result == null) {
				textarea_result.setText("");
				return;
			}

			// HTML code
			if (result.startsWith("<")) {
				//				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				//				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				//				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
				//
				//				// Transform String to StringWriter
				//				StringWriter strWriter = new StringWriter();
				//				strWriter.write(result);
				//
				//				// initialize StreamResult with File object to save to file
				//				StreamResult res = new StreamResult(strWriter);
				//				DOMSource source = new DOMSource();
				//				transformer.transform(source, res);
				//				String xmlString = res.getWriter().toString();

				textarea_result.setText(result);
				return;
			}

			// JSON code
			ObjectMapper mapper = new ObjectMapper();
			Object json = mapper.readValue(result, Object.class);
			String indented = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
			textarea_result.setText(indented);
		} catch (ConnectException e) {
			textarea_result.setText("");
			txt_error.setText("L'URL est inaccessible");
			txt_status.setText("ERR_CONNECTION_REFUSED");
			return;
		} catch (MalformedURLException e) {
			textarea_result.setText("");
			txt_error.setText("L'URL est invalide");
			txt_status.setText("URL_SYNTAX_ERROR");
			return;
		} catch (PatternSyntaxException e) {
			textarea_result.setText("");
			txt_error.setText("Aucun paramètre POST spécifié");
			txt_status.setText("URL_SYNTAX_ERROR");
			return;
		} catch (IOException | TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		}
	}

}
