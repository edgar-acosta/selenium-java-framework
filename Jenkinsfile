pipeline {
    agent any

    tools {
        // Usa los nombres que configuraste en "Global Tool Configuration"
        maven 'MAVEN_HOME'
        jdk 'JAVA_HOME'
    }

    stages {
        stage('Checkout') {
            steps {
                // Jenkins descargará el código solo
                echo 'Descargando código de GitHub...'
            }
        }

        stage('Build & Compile') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('UI Tests (Selenium)') {
            steps {
                script {
                    // Este comando "unset" limpia las librerías del Snap 
                    // para que Chrome use las del sistema operativo
                    sh '''
                        export LD_LIBRARY_PATH=
                        export PATH=$PATH:/usr/bin
                        google-chrome --version || echo "Chrome sigue protestando pero intentaremos seguir"
                        mvn test
                    '''
                }
            }
        }
    }

    post {
        always {
            echo 'Archivando resultados de las pruebas...'
            // Esto guarda los archivos XML y HTML para que los veas en el Dashboard
            junit 'target/surefire-reports/*.xml'
            archiveArtifacts artifacts: 'target/surefire-reports/**', allowEmptyArchive: true
            // 'Allure_Report' debe coincidir con el nombre que pusiste en Tools
            allure includeProperties: false, jdk: '', results: [[path: 'Allure_Report']]
        }
    }
}