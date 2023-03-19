# Changelog

## [Unreleased]

### Changed

- Move **CompileMain** class from **webapp** project to **compiler** project ([#70](https://github.com/francois/mindcode/issues/70))
- Improve optimizations removing redundant temporary variables to handle more cases (
[#52](https://github.com/francois/mindcode/issues/52),
[#78](https://github.com/francois/mindcode/pull/78),
[#79](https://github.com/francois/mindcode/pull/79),
[#80](https://github.com/francois/mindcode/pull/80)
)


### Added

- Add optimization eliminating jumps to next instruction ([#50](https://github.com/francois/mindcode/issues/50))
- Add optimization replacing jump chains with single jump ([#76](https://github.com/francois/mindcode/pull/76))
- Add optimization eliminating inaccessible code ([#77](https://github.com/francois/mindcode/pull/77))
- Add output messages for optimizers

### Removed

### Fixed

- Fix getlink() arguments being removed by optimization ([#59](https://github.com/francois/mindcode/issues/59))
- Fix optimization not handling sequences of `op` instructions ([#61](https://github.com/francois/mindcode/pull/61))
- Fix optimization removing an assignment to a variable ([#62](https://github.com/francois/mindcode/issues/62))
- Fix `else` branch mandatory after `elsif` ([#65](https://github.com/francois/mindcode/issues/65))
- Fix optimization replacing strictEqual with notEqual ([#64](https://github.com/francois/mindcode/issues/64))


