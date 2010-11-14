import sbt.{DefaultProject, ProjectInfo}

/**
 * Created by IntelliJ IDEA.
 * User: przemek
 * Date: 08.11.10
 * Time: 23:36
 * To change this template use File | Settings | File Templates.
 */

class Wrasse3(info: ProjectInfo) extends DefaultProject(info) with IdeaProject
{
  val specs = "org.scala-tools.testing" % "specs_2.8.0" % "1.6.5" % "test"
  val mockito = "org.mockito" % "mockito-all" % "1.8.5" % "test"
}

