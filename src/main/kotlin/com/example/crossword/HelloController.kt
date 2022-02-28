package com.example.crossword

import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import javafx.animation.TranslateTransition
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Orientation
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.VBox
import javafx.util.Duration
import java.awt.Desktop
import java.io.File
import java.net.URI
import java.net.URL
import java.util.*


class HelloController : Initializable {

    @FXML
    private lateinit var anchorPane: AnchorPane

    @FXML
    private lateinit var vbox: VBox

    @FXML
    private lateinit var answerLayout: VBox

    @FXML
    private lateinit var confirmAnswerButton: Button

    @FXML
    private lateinit var showAnswerButton: Button

    @FXML
    private lateinit var answerField: TextField

    @FXML
    private lateinit var showAnswers: Button
    @FXML
    private lateinit var clearButton: Button
    @FXML
    private lateinit var githubButton: Button

    private var mutableMap = mutableMapOf<Pair<Int, Int>, TextField>()

    private val questions = GsonBuilder()
        .excludeFieldsWithoutExposeAnnotation()
        .create()
        .fromJson(File("questions.json").readText(), Array<Question>::class.java)

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        questions.forEachIndexed { index, question ->
            var textField = createTextField(question.startPosX, question.startPosY, "${index + 1}")

            textField = mutableMap.getOrPut(question.startPosX to question.startPosY) {
                anchorPane.children += textField
                textField
            }
            val textFields = mutableListOf(textField)

            question.questionTextField = TextField("${index + 1}: ${question.question}").apply {
                prefHeight = 30.0
                prefWidth = 400.0

                isEditable = false
            }

            vbox.children += question.questionTextField!!

            for (i in 1 until question.answer.length) {
                val x =
                    if (question.orientation == Orientation.HORIZONTAL) question.startPosX + i else question.startPosX
                val y = if (question.orientation == Orientation.VERTICAL) question.startPosY + i else question.startPosY

                textField = createTextField(x, y)

                textField = mutableMap.getOrPut(x to y) {
                    anchorPane.children += textField
                    textField
                }

                textFields.add(textField)
            }

            question.textFields = textFields

            question.textFields.forEach { field ->
                field.setOnMouseClicked {
                    question.questionTextField!!.requestFocus()

                    showDialog(question)
                }
            }
        }

        showAnswers.setOnAction {
            questions.forEach { question ->
                question.answer.forEachIndexed { index, letter ->
                    question.textFields[index].text = letter.toString()
                }
            }
        }

        clearButton.setOnAction {
            questions.forEach { question ->
                question.answer.forEachIndexed { index, _ ->
                    question.textFields[index].text = ""
                }
            }
        }

        githubButton.setOnAction {
            Desktop.getDesktop().browse(URI("https://github.com/LikCoD/CrosswordCreator"))
        }
    }

    private fun createTextField(posX: Int, posY: Int, prompt: String = ""): TextField =
        TextField().apply {
            layoutX = 25.0 + posX * 30.0
            layoutY = 25.0 + posY * 30.0

            prefHeight = 30.0
            prefWidth = 30.0

            isEditable = false
            promptText = prompt
        }

    private fun showDialog(question: Question) {
        answerLayout.isVisible = true

        confirmAnswerButton.setOnAction {
            if (answerField.text.lowercase() != question.answer.lowercase()) {
                warning(answerField)
                return@setOnAction
            }

            question.answer.forEachIndexed { index, letter ->
                question.textFields[index].text = letter.toString()
            }

            answerField.text = ""
            answerLayout.isVisible = false
        }

        showAnswerButton.setOnAction {
            answerField.text = question.answer
        }
    }

    data class Question(
        @Expose(serialize = true)
        val question: String,
        @Expose(serialize = true)
        val answer: String,
        @Expose(serialize = true)
        val orientation: Orientation,
        @Expose(serialize = true)
        val startPosX: Int,
        @Expose(serialize = true)
        val startPosY: Int,
        var textFields: List<TextField> = emptyList(),
        var questionTextField: TextField? = null
    )

    private fun warning(node: Node, style: String = "-fx-background-color: rgb(225,170,0);") {
        val tt = TranslateTransition(Duration.millis(50.0), node)
        val keyTyped = node.onKeyTyped

        node.style += style

        if (node is TextField)
            node.setOnKeyTyped {
                node.style = node.style.replace(style, "")

                if (keyTyped != null) {
                    keyTyped.handle(it)
                    node.onKeyTyped = keyTyped
                }

            }

        tt.fromX = 0.0
        tt.toX = 5.0
        tt.cycleCount = 6
        tt.isAutoReverse = true
        tt.play()
    }
}