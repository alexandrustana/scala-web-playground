package com.local.quickstart.infrastructure.util.validation

import cats.Semigroup
import cats.data.Validated
import cats.data.Validated._
import cats.syntax.semigroup._
import cats.syntax.apply._     // for mapN
import cats.syntax.validated._ // for Monad

/**
  * @author Alexandru Stana, alexandru.stana@busymachines.com
  * @since 16/04/2018
  */
sealed trait Predicate[E, A] {
  import Predicate._

  def run(implicit s: Semigroup[E]): A => Either[E, A] =
    (a: A) => this(a).toEither

  def and(that: Predicate[E, A]): Predicate[E, A] =
    And(this, that)

  def or(that: Predicate[E, A]): Predicate[E, A] =
    Or(this, that)

  def apply(a: A)(implicit s: Semigroup[E]): Validated[E, A] =
    this match {
      case Pure(func) =>
        func(a)

      case And(left, right) =>
        (left(a), right(a)).mapN((_, _) => a)

      case Or(left, right) =>
        left(a) match {
          case Valid(_) => Valid(a)
          case Invalid(e1) =>
            right(a) match {
              case Valid(_)    => Valid(a)
              case Invalid(e2) => Invalid(e1 |+| e2)
            }
        }
    }
}

object Predicate {
  final case class And[E, A](left: Predicate[E, A], right: Predicate[E, A]) extends Predicate[E, A]

  final case class Or[E, A](left: Predicate[E, A], right: Predicate[E, A]) extends Predicate[E, A]

  final case class Pure[E, A](func: A => Validated[E, A]) extends Predicate[E, A]

  def apply[E, A](f: A => Validated[E, A]): Predicate[E, A] =
    Pure(f)

  def lift[E, A](err: E, fn: A => Boolean): Predicate[E, A] =
    Pure(a => if (fn(a)) a.valid else err.invalid)
}