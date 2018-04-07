package com.local.quickstart.domain

import scala.language.higherKinds

/**
  * @author Alexandru Stana, alexandru.stana@busymachines.com
  * @since 07/04/2018
  */
trait GenericRepositoryAlgebra[F[_], A] {
  def create(o: A): F[A]
}