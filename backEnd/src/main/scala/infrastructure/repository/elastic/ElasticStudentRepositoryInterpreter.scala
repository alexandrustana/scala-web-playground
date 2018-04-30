package infrastructure.repository.elastic

import cats.Monad
import cats.implicits._
import com.sksamuel.elastic4s.http._
import domain.student.{Student, StudentRepositoryAlgebra}
import util.MapT._

/**
  * @author Alexandru Stana, alexandru.stana@busymachines.com
  * @since 11/04/2018
  */
class ElasticStudentRepositoryInterpreter[F[_]: Monad](edb: HttpClient) extends StudentRepositoryAlgebra[F] {

  import com.sksamuel.elastic4s.http.ElasticDsl._

  override def create(o: Student): F[Student] =
    edb.execute {
      index("student" -> "type") fields o
        .toMap[Map[String, Any]]
    }.await match {
      case Left(e)  => throw new Exception(e.error.reason)
      case Right(_) => o.pure[F]
    }

  override def getAll: F[List[Student]] =
    edb.execute {
      search("student")
    }.await match {
      case Left(e) => throw new Exception(e.error.reason)
      case Right(v) =>
        v.result.hits.hits.map(_.sourceAsMap).toList.mapTo[List[Student]].pure[F]
    }
}

object ElasticStudentRepositoryInterpreter {

  def apply[F[_]: Monad](edb: HttpClient): ElasticStudentRepositoryInterpreter[F] =
    new ElasticStudentRepositoryInterpreter(edb)
}