package app.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.ResourceBundle;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.fasterxml.jackson.databind.ObjectMapper;
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
	private TextField textfield_url;

	@FXML
	private ComboBox<String> combo_method;

	@FXML
	private Button btn_send;

	@FXML
	private JFXTextArea textarea_result;

	@FXML
    private Text msg_error;

	@Override
	public void initialize(URL location, ResourceBundle rb) {
		textfield_url.setText("http://localhost:5000/api/users");

		combo_method.getItems().add("GET");
		combo_method.getItems().add("POST");
	}

	@FXML
	void onMouseClicked(MouseEvent event) {
		if (textfield_url.getText().length() <= 5) {
			msg_error.setText("Veuillez spécifier une URL");// error message under send button
			return;
		}

		msg_error.setText("");
		try {
			String result = RequestController.sendGET(textfield_url.getText());
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
		} catch (IOException | TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		}
	}

}
